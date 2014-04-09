package com.muzima

class PersonAddress {

    boolean preferred = false
    String address1
    String address2
    String address3
    String address4
    String address5
    String address6

    String cityVillage
    String countyDistrict
    String stateProvince
    String postalCode
    String country

    String latitude
    String longitude

    static belongsTo = [person: Person]

    static constraints = {
        address1 nullable: false, blank: false
        address2 nullable: true
        address3 nullable: true
        address4 nullable: true
        address5 nullable: true
        address6 nullable: true

        cityVillage nullable: true
        countyDistrict nullable: true
        stateProvince nullable: true
        postalCode nullable: true
        country nullable: true

        latitude nullable: true
        longitude nullable: true
    }

    void updatePersonAddress(PersonAddress personAddress) {
        this.address1 = personAddress.address1
        this.address2 = personAddress.address2
        this.address3 = personAddress.address3
        this.address4 = personAddress.address4
        this.address5 = personAddress.address5
        this.address6 = personAddress.address6

        this.cityVillage = personAddress.cityVillage
        this.countyDistrict = personAddress.countyDistrict
        this.stateProvince = personAddress.stateProvince
        this.postalCode = personAddress.postalCode
        this.country = personAddress.country

        this.latitude = personAddress.latitude
        this.longitude = personAddress.longitude
    }
}
