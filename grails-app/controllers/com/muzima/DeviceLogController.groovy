package com.muzima

import grails.plugin.springsecurity.annotation.Secured

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import grails.transaction.Transactional

@Transactional(readOnly = true)
class DeviceLogController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def convert(DeviceLog deviceLogInstance) {
        def device = [
                id    : deviceLogInstance.id,
                log   : deviceLogInstance.log,
                device: [
                        id: deviceLogInstance.device.id
                ]
        ]
        return device;
    }

    @Secured(['isFullyAuthenticated()'])
    def index() {
        def deviceLogMap = []
        def deviceLogCount = 0
        if (params.query?.trim()) {
            DeviceLog.createCriteria().listDistinct() {
                firstResult: params.offset
                maxResults: params.max
                createAlias("device", "device")
                eq("device.id", params.deviceId)
            }.each {
                deviceLogMap.add(convert(it))
            }

            deviceLogCount =
                    DeviceLog.createCriteria().list() {
                        createAlias("device", "device")
                        eq("device.id", params.deviceId)
                        projections {
                            countDistinct("id")
                        }
                    }
        }

        render(contentType: "application/json") {
            count = deviceLogCount
            results = deviceLogMap
        }
    }

    @Secured(['isFullyAuthenticated()'])
    def show() {
        def deviceLogInstance = DeviceLog.get(params.id)
        render(contentType: "application/json") {
            convert(deviceLogInstance)
        }
    }

    @Secured(['isFullyAuthenticated()'])
    @Transactional
    def save() {
        def json = request.JSON
        def deviceLogInstance = new DeviceLog(json)
        if (deviceLogInstance == null) {
            notFound()
            return
        }

        def registrationKey = json["regid"]
        def device = Device.findByRegistrationKey(registrationKey)

        deviceLogInstance.setLog(json)
        deviceLogInstance.setDevice(device)

        deviceLogInstance.save(flush: true, failOnError: true)
        response.status = CREATED.value();
        render(contentType: "application/json") {
            convert(deviceLogInstance)
        }
    }

    protected void notFound() {
        render status: NOT_FOUND
    }

}
