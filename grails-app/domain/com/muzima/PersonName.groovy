package com.muzima

class PersonName {

    boolean preferred = false
    String prefix
    String givenName
    String middleName
    String familyName
    String degree

    static belongsTo = [person: Person]

    static constraints = {
        prefix nullable: true
        givenName nullable: false, blank: false
        middleName nullable: true
        familyName nullable: false, blank: false
        degree nullable: true
    }

    void updatePersonName(PersonName personName) {
        this.preferred = personName.preferred

        this.prefix = personName.prefix
        this.givenName = personName.givenName
        this.middleName = personName.middleName
        this.familyName = personName.familyName
        this.degree = personName.degree
    }
}
