package com.muzima

class Device {

    String imei
    String sim
    String name
    String description
    Date purchasedDate
    String status
    DeviceType deviceType

    static belongsTo = [institution: Institution]

    static mapping = {
        description column: "description", sqlType: "varchar", length: 1024
    }

    static constraints = {
        imei nullable: false, blank: false
        description nullable: false, blank: false
        sim nullable: false, blank: false
        purchasedDate nullable: false, max: new Date()
        deviceType nullable: false
        status nullable: false, blank: false
    }
}
