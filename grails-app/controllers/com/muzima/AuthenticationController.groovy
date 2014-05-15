package com.muzima

import grails.plugin.springsecurity.annotation.Secured

import static org.springframework.http.HttpStatus.OK

class AuthenticationController {

    def springSecurityService

    def convert(User userInstance) {
        def user = [
                id      : userInstance.id,
                username: userInstance.username
        ]

        def person = userInstance.getPerson()
        if (person != null) {
            def personName = person.getPersonName()
            if (personName != null) {
                user.put("givenName", personName.getGivenName())
                user.put("middleName", personName.getMiddleName())
                user.put("familyName", personName.getFamilyName())
            }
        } else {
            user.put("givenName", "Super")
            user.put("familyName", "User")
        }
        return user
    }

    @Secured(["isAuthenticated()"])
    def index() {
        def userInstance = springSecurityService.getCurrentUser()
        response.status = OK.value()
        render(contentType: "application/json") {
            convert(userInstance)
        }
    }
}
