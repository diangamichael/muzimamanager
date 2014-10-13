package com.muzima

import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.OK

class UserController {

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

    @Secured(["isFullyAuthenticated()"])
    def index() {
        def userInstance = springSecurityService.getCurrentUser()
        response.status = OK.value()
        render(contentType: "application/json") {
            convert(userInstance)
        }
    }

    @Secured(['isFullyAuthenticated()'])
    def show() {
        def personInstance = Person.get(params.id)
        def userInstance = User.findByPerson(personInstance)
        if (userInstance == null) {
            notFound()
            return
        }
        render(contentType: "application/json") {
            convert(userInstance)
        }
    }

    @Secured(['isFullyAuthenticated()'])
    @Transactional
    def save() {
        def json = request.JSON
        def id = json["id"]
        def username = json["username"]
        def password = json["password"]

        def personInstance = Person.get(id)
        if (personInstance == null) {
            notFound()
            return
        }

        def userInstance = User.findByUsername(username)
        if (userInstance == null) {
            userInstance = new User(username: username, password: password, person: personInstance)
            userInstance.save(flush: true)
            response.status = CREATED.value()
            render(contentType: "application/json") {
                convert(userInstance)
            }
        } else {
            response.status = BAD_REQUEST.value();
        }
    }

    @Secured(['isFullyAuthenticated()'])
    @Transactional
    def update() {
        def json = request.JSON
        println(json)
        def id = json["id"]
        def username = json["username"]
        def password = json["password"]
        println(id + ", " + username + ", " + password)

        def personInstance = Person.get(id)
        println(personInstance)
        if (personInstance == null) {
            notFound()
            return
        }

        def userInstance = User.findByUsername(username)
        println(userInstance)
        if (userInstance == null) {
            notFound()
        } else {
            userInstance.setPassword(password);
            userInstance.save(flush: true)
            response.status = OK.value()
            render(contentType: "application/json") {
                convert(userInstance)
            }
        }
    }

    protected void notFound() {
        render status: NOT_FOUND
    }
}
