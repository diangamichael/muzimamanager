package com.muzima

import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.OK

@Transactional(readOnly = true)
class DeviceTypeController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def convert(DeviceType deviceTypeInstance) {
        def deviceType = [
                id           : deviceTypeInstance.id,
                name         : deviceTypeInstance.name,
                description  : deviceTypeInstance.description,
                deviceDetails: deviceTypeInstance.deviceDetails.collect {
                    [
                            id           : it.id,
                            category     : it.category,
                            subCategory  : it.subCategory,
                            categoryValue: it.categoryValue
                    ]
                }
        ]
        return deviceType
    }

    @Secured(['isFullyAuthenticated()'])
    def index() {
        def deviceTypeMap = []
        def deviceTypeCount = 0
        if (params.query?.trim()) {
            DeviceType.createCriteria().listDistinct() {
                firstResult: params.offset
                maxResults: params.max
                createAlias("deviceDetails", "deviceDetails")
                or {
                    ilike("name", "%" + params.query + "%")
                    ilike("description", "%" + params.query + "%")
                    ilike("deviceDetails.categoryValue", "%" + params.query + "%")
                }
            }.each {
                deviceTypeMap.add(convert(it))
            }

            deviceTypeCount =
                    DeviceType.createCriteria().list() {
                        createAlias("deviceDetails", "deviceDetails")
                        or {
                            ilike("name", "%" + params.query + "%")
                            ilike("description", "%" + params.query + "%")
                            ilike("deviceDetails.categoryValue", "%" + params.query + "%")
                        }
                        projections {
                            countDistinct("id")
                        }
                    }
        } else {
            DeviceType.list(params).each {
                deviceTypeMap.add(convert(it))
            }

            deviceTypeCount = DeviceType.count()
        }

        render(contentType: "application/json") {
            count = deviceTypeCount
            results = deviceTypeMap
        }
    }

    @Secured(['isFullyAuthenticated()'])
    def show() {
        def deviceTypeInstance = DeviceType.get(params.id)
        if (deviceTypeInstance == null) {
            notFound()
            return
        }
        render(contentType: "application/json") {
            convert(deviceTypeInstance)
        }
    }

    @Secured(['isFullyAuthenticated()'])
    @Transactional
    def save() {
        def deviceTypeInstance = new DeviceType()

        def json = request.JSON
        def deviceType = new DeviceType(json)
        deviceTypeInstance.updateDeviceType(deviceType)
        json["deviceDetails"].each {
            def deviceDetail = new DeviceDetail(it)
            deviceTypeInstance.addToDeviceDetails(deviceDetail)
        }
        deviceTypeInstance.save(flush: true, failOnError: true)
        response.status = OK.value()
        render(contentType: "application/json") {
            convert(deviceTypeInstance)
        }
    }

    @Secured(['isFullyAuthenticated()'])
    @Transactional
    def update() {
        def json = request.JSON
        def deviceTypeInstance = DeviceType.get(json["id"])
        if (deviceTypeInstance == null) {
            notFound()
            return
        }

        def deviceType = new DeviceType(json)
        deviceTypeInstance.updateDeviceType(deviceType)

        def deviceDetails = deviceTypeInstance.deviceDetails
        json["deviceDetails"].each {
            def deviceDetailId = it["id"]
            def deviceDetail = new DeviceDetail(it)
            if (deviceDetailId != null) {
                def deviceDetailInstance = deviceDetails.find({ it.id == deviceDetailId })
                deviceDetailInstance.updateDeviceDetail(deviceDetail)
            } else {
                deviceTypeInstance.addToDeviceDetails(deviceDetail)
            }
        }
        deviceTypeInstance.save(flush: true, failOnError: true)
        response.status = OK.value()
        render(contentType: "application/json") {
            convert(deviceTypeInstance)
        }
    }

    protected void notFound() {
        render status: NOT_FOUND
    }
}
