package com.muzima

import grails.transaction.Transactional

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
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
        return deviceType;
    }

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
        }

        render(contentType: "application/json") {
            count = deviceTypeCount
            results = deviceTypeMap
        }
    }

    def show() {
        def deviceTypeInstance = DeviceType.get(params.id)
        render(contentType: "application/json") {
            convert(deviceTypeInstance)
        }
    }

    @Transactional
    def save() {
        def deviceType = request.JSON
        def deviceTypeInstance = new DeviceType(deviceType)
        if (deviceTypeInstance == null) {
            notFound()
            return
        }
        for (deviceDetail in deviceType["deviceDetails"]) {
            def deviceDetailInstance = new DeviceDetail(deviceDetail)
            deviceTypeInstance.addToDeviceDetails(deviceDetailInstance)
        }
        deviceTypeInstance.save(flush: true, failOnError: true)
        response.status = CREATED.value();
        render(contentType: "application/json") {
            convert(deviceTypeInstance)
        }
    }

    @Transactional
    def update() {
        def deviceType = request.JSON
        def deviceTypeInstance = new DeviceType(deviceType)
        if (deviceTypeInstance == null) {
            notFound()
            return
        }
        for (deviceDetail in deviceType["deviceDetails"]) {
            def deviceDetailInstance = new DeviceDetail(deviceDetail)
            deviceTypeInstance.addToDeviceDetails(deviceDetailInstance)
        }
        deviceTypeInstance.save(flush: true, failOnError: true)
        response.status = OK.value();
        render(contentType: "application/json") {
            convert(deviceTypeInstance)
        }
    }

    @Transactional
    def delete(DeviceType deviceTypeInstance) {
        if (deviceTypeInstance == null) {
            notFound()
            return
        }
        deviceTypeInstance.delete flush: true
        render status: NO_CONTENT
    }

    protected void notFound() {
        render status: NOT_FOUND
    }
}
