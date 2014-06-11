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
        def messageStatus = androidGcmService.sendMessage(message, [deviceInstance.registrationKey], "[mUzima command center]", apiKey)
        log.info messageStatus

        render status: OK
    }

    @Secured(['isAnonymous()'])
    def save() {
        println request.JSON
        def json = request.JSON
        def operation = json["operation"]
        if (operation == "register") {
            def deviceInstance = Device.findByImei(json["imei"])
            if (deviceInstance == null) {
                deviceInstance = new Device()
                deviceInstance.setSim(json["sim"])
                deviceInstance.setImei(json["imei"])
                deviceInstance.setPurchasedDate(new Date())

                def deviceTypeInstance = DeviceType.findByName(json["type"])
                deviceInstance.setDeviceType(deviceTypeInstance)

                def count = Device.countByDeviceType(deviceTypeInstance)
                deviceInstance.setName(json["type"] + " #" + count)

                Institution.all.each {
                    // TODO: (hack) we're assuming we have only 1 institution for now in each installation.
                    deviceInstance.setInstitution(it)
                }
                deviceInstance.setDescription("_BLANK_")
            }
            deviceInstance.setRegistrationKey(json["regid"])
            deviceInstance.setStatus("Registered")
            deviceInstance.save(flush: true, failOnError: true)
        } else if (operation == "unregister") {
            def deviceInstance = Device.findByRegistrationKey(json["regid"])
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
