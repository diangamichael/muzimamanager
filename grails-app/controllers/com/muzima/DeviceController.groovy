package com.muzima

import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.OK

@Transactional(readOnly = true)
class DeviceController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def convert(Device deviceInstance) {
        def device = [
                id             : deviceInstance.id,
                imei           : deviceInstance.imei,
                sim            : deviceInstance.sim,
                name           : deviceInstance.name,
                description    : deviceInstance.description,
                purchasedDate  : deviceInstance.purchasedDate.time,
                status         : deviceInstance.status,
                registrationKey: deviceInstance.registrationKey,
                deviceType     : [
                        id           : deviceInstance.deviceType.id,
                        name         : deviceInstance.deviceType.name,
                        description  : deviceInstance.deviceType.description,
                        deviceDetails: deviceInstance.deviceType.deviceDetails.collect {
                            [
                                    id           : it.id,
                                    category     : it.category,
                                    subCategory  : it.subCategory,
                                    categoryValue: it.categoryValue
                            ]
                        }
                ]
        ]
        return device;
    }

    @Secured(['isFullyAuthenticated()'])
    def index() {
        def deviceMap = []
        def deviceCount = 0
        if (params.query?.trim()) {
            Device.createCriteria().listDistinct() {
                firstResult: params.offset
                maxResults: params.max
                createAlias("deviceType", "deviceType")
                and {
                    or {
                        ilike("imei", "%" + params.query + "%")
                        ilike("sim", "%" + params.query + "%")
                        ilike("name", "%" + params.query + "%")
                        ilike("deviceType.name", "%" + params.query + "%")
                    }
                    ne("voided",true)
                }
            }.each {
                deviceMap.add(convert(it))
            }

            deviceCount =
                    Device.createCriteria().list() {
                        createAlias("deviceType", "deviceType")
                        and {
                            or {
                                ilike("imei", "%" + params.query + "%")
                                ilike("sim", "%" + params.query + "%")
                                ilike("name", "%" + params.query + "%")
                                ilike("deviceType.name", "%" + params.query + "%")
                            }
                            ne("voided",true)
                        }
                        projections {
                            countDistinct("id")
                        }
                    }
        }

        render(contentType: "application/json") {
            count = deviceCount
            results = deviceMap
        }
    }

    @Secured(['isFullyAuthenticated()'])
    def show() {
        def deviceInstance = Device.get(params.id)
        if (deviceInstance == null) {
            notFound()
            return
        }
        render(contentType: "application/json") {
            convert(deviceInstance)
        }
    }

    @Secured(['isFullyAuthenticated()'])
    @Transactional
    def save() {
        def json = request.JSON
        def deviceInstance = new Device(json)
        if (deviceInstance == null) {
            notFound()
            return
        }

        Institution.all.each {
            // TODO: (hack) we're assuming we have only 1 institution for now in each installation.
            deviceInstance.setInstitution(it)
        }

        deviceInstance.save(flush: true, failOnError: true)
        response.status = CREATED.value();
        render(contentType: "application/json") {
            convert(deviceInstance)
        }
    }

    @Secured(['isFullyAuthenticated()'])
    @Transactional
    def update() {
        def json = request.JSON
        def deviceInstance = Device.get(json["id"])
        if (deviceInstance == null) {
            notFound()
            return
        }

        def jsonDevice = new Device(json)
        deviceInstance.updateDevice(jsonDevice)

        def deviceType = json["deviceType"]
        def deviceTypeInstance = DeviceType.get(deviceType["id"])
        deviceInstance.setDeviceType(deviceTypeInstance)

        deviceInstance.save(flush: true, failOnError: true)
        response.status = OK.value();
        render(contentType: "application/json") {
            convert(deviceInstance)
        }
    }

    protected void notFound() {
        render status: NOT_FOUND
    }

}
