package com.muzima

import grails.plugin.springsecurity.annotation.Secured
import org.codehaus.groovy.grails.web.json.JSONObject

import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.EXPECTATION_FAILED

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
            def personInstance
            def identifier = json["identifier"]
            if (identifier != null && identifier.trim()) {
                personInstance = Person.findByIdentifier(identifier)
            }

            // if person instance is null, return with error
            if (personInstance == null) {
                render(contentType: "application/json", status: EXPECTATION_FAILED) {
                    errorMessage = "Invalid User Specified. Confirm your Unique ID"
                }
                return;
            }

            def deviceInstance = Device.findByImei(json["imei"])
            if (deviceInstance == null) {
                deviceInstance = new Device()
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
            // always save SIM, it may have changed
            def sim = json["sim"]
            if (!JSONObject.NULL.equals(sim))
                deviceInstance.setSim(sim)

            // just in case devise was previously voided, un-void it
            deviceInstance.setVoided(false)
            deviceInstance.setDateVoided(null)
            deviceInstance.setVoidedReason(null)

            deviceInstance.setRegistrationKey(json["regid"])
            deviceInstance.setStatus("Registered")
            if (!deviceInstance.save(flush: true)){
                render(contentType: "application/json", status: EXPECTATION_FAILED) {
                    errorMessage = "Not all required parameters were found"
                }
                return;
            }

            if (personInstance != null && deviceInstance != null) {
                def assignmentInstance = Assignment.findByDevice(deviceInstance)
                if (assignmentInstance == null) {
                    assignmentInstance = new Assignment()
                }
                assignmentInstance.setDevice(deviceInstance)
                assignmentInstance.setPerson(personInstance)

                // just in case devise was previously voided, un-void it
                assignmentInstance.setVoided(false)
                assignmentInstance.setDateVoided(null)
                assignmentInstance.setVoidedReason(null)

                assignmentInstance.save flush: true, failOnError: true
                def personName = personInstance.personName
                render(contentType: "application/json", status: OK) {
                    identifier = personInstance.identifier
                    givenName = personName.givenName
                    middleName = personName.middleName
                    familyName = personName.familyName
                }
                return;
            }

        } else if (operation == "unregister") {
            def deviceInstance = Device.findByRegistrationKey(json["regid"])
            if (deviceInstance == null) {
                notFound()
                return
            }
            deviceInstance.setRegistrationKey("000000000000000")
            deviceInstance.setStatus("Unregistered")
            deviceInstance.save(flush: true, failOnError: true)
        }
        render status: OK
    }

    protected void notFound() {
        render status: NOT_FOUND
    }
}
