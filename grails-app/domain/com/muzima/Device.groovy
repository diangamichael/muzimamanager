package com.muzima

class Device {

    String imei
    String sim
    String name
    String description
    String registrationKey
    Date purchasedDate
    String status
    DeviceType deviceType

    static belongsTo = [institution: Institution]

    static mapping = {
        registrationKey column: "registration_key", sqlType: "varchar", length: 1024
        description column: "description", sqlType: "varchar", length: 1024
    }

    static constraints = {
        imei nullable: false, blank: false, unique: true
        sim nullable: false, blank: false, unique: true
        name nullable: false, blank: false
        description nullable: false, blank: false
        registrationKey nullable: true, blank: true
        purchasedDate nullable: false, max: new Date()
        deviceType nullable: false
        status nullable: false, blank: false
    }

    void updateDevice(Device device) {
        this.imei = device.imei
        this.sim = device.sim
        this.name = device.name
        this.description = device.description
        this.purchasedDate = device.purchasedDate
        this.status = device.status
    }
}
