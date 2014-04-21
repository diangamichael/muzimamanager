package com.muzima

class DeviceDetail {

    String category
    String subCategory
    String categoryValue

    static belongsTo = [deviceType: DeviceType]

    static mapping = {
        category sqlType: "varchar", length: 1024
        subCategory sqlType: "varchar", length: 1024
        categoryValue sqlType: "varchar", length: 1024
    }

    static constraints = {
        category nullable: false, blank: false
        subCategory nullable: false, blank: false
        categoryValue nullable: false, blank: false
    }

    void updateDeviceDetail(DeviceDetail deviceDetail) {
        this.category = deviceDetail.category
        this.subCategory = deviceDetail.subCategory
        this.categoryValue = deviceDetail.categoryValue
    }
}
