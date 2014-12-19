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

    Boolean voided = false
    Date dateVoided
    String voidedReason

    static belongsTo = [institution: Institution]

    static constraints = {
        imei nullable: false, blank: false, unique: true
        registrationKey maxSize: 1024
        description maxSize: 1024
        // optional sim because of the wifi only on Nexus 7
        sim nullable: true, blank: true, unique: false
        name nullable: false, blank: false
        description nullable: false, blank: false
        registrationKey nullable: false, blank: true
        purchasedDate nullable: false
        deviceType nullable: false
        status nullable: false, blank: false

        voided nullable: false
        dateVoided nullable: true
        voidedReason nullable: true
    }

    void updateDevice(Device device) {
        this.imei = device.imei
        this.sim = device.sim
        this.name = device.name
        this.description = device.description
        this.purchasedDate = device.purchasedDate
        this.status = device.status

        this.voided = device.voided
        this.dateVoided = device.dateVoided
        this.voidedReason = device.voidedReason
    }
}
