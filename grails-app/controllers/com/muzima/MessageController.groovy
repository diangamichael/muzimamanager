package com.muzima

import grails.plugin.springsecurity.annotation.Secured

import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.OK

class MessageController {

    static allowedMethods = [save: "POST", delete: "DELETE"]

    def androidGcmService

    @Secured(['isFullyAuthenticated()'])
    def index() {
        println(params);
        def deviceInstance = Device.get(params.deviceId);
        if (deviceInstance == null) {
            notFound()
            return
        }

        def apiKey = "AIzaSyBZteShRq5BsZADpC6KO9BFkIDLhA4PhUE"
        def message = [command: params.command]
        androidGcmService.sendMessage(message, [deviceInstance.registrationKey], "[mUzima command center]", apiKey)

        render status: OK
    }

    @Secured(['isFullyAuthenticated()'])
    def save() {
        def json = request.JSON
        def operation = json["operation"]
        if (operation == "register") {
            def deviceInstance = Device.findByImei(json["imei"])
            if (deviceInstance == null) {
                notFound()
                return
            }
            deviceInstance.setRegistrationKey(json["regId"])
            deviceInstance.setStatus("Registered")
            deviceInstance.save(flush: true, failOnError: true)
        } else if (operation == "unregister") {
            def deviceInstance = Device.findByRegistrationKey(json["regId"])
            if (deviceInstance == null) {
                notFound()
                return
            }
            deviceInstance.setRegistrationKey(null)
            deviceInstance.setStatus("Unregistered")
            deviceInstance.save(flush: true, failOnError: true)
        }

        // TODO: automate finding person by the username or email address and then create the assignment

        render status: OK
    }

    protected void notFound() {
        render status: NOT_FOUND
    }
}
