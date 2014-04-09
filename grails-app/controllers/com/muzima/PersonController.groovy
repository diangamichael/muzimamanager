package com.muzima

import grails.transaction.Transactional

import javax.persistence.criteria.JoinType

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK

@Transactional(readOnly = true)
class PersonController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    // to curl this with cors and requesting json
    // curl -H "Origin: http://example.com" -H "Accept: application/json" --verbose http://localhost:8080/device-simple/api/person/1
    // curl -H "Origin: http://example.com" -H "Accept: application/json" --verbose http://localhost:8080/device-simple/api/person

    def convert(Person personInstance) {
        def person = [
                id             : personInstance.id,
                gender         : personInstance.gender,
                birthdate      : personInstance.birthdate.format("dd-MMM-yyyy"),
                personNames    : personInstance.personNames.collect {
                    [
                            id        : it.id,
                            preferred : it.preferred,
                            prefix    : it.prefix,
                            givenName : it.givenName,
                            middleName: it.middleName,
                            familyName: it.familyName,
                            degree    : it.degree
                    ]
                },
                personAddresses: personInstance.personAddresses.collect {
                    [
                            id            : it.id,
                            preferred     : it.preferred,
                            address1      : it.address1,
                            address2      : it.address2,
                            address3      : it.address3,
                            address4      : it.address4,
                            address5      : it.address5,
                            address6      : it.address6,
                            cityVillage   : it.cityVillage,
                            countyDistrict: it.countyDistrict,
                            stateProvince : it.stateProvince,
                            postalCode    : it.postalCode,
                            country       : it.country,
                            latitude      : it.latitude,
                            longitude     : it.longitude
                    ]
                }
        ]
        return person;
    }

    def index() {
        def personMap = []
        def personCount = 0
        if (params.query?.trim()) {
            Person.createCriteria().listDistinct() {
                firstResult: params.offset
                maxResults: params.max
                personNames(JoinType.LEFT) {
                    or {
                        ilike("givenName", "%" + params.query + "%")
                        ilike("familyName", "%" + params.query + "%")
                    }
                }
            }.each {
                personMap.add(convert(it))
            }
            personCount = Person.createCriteria().get {
                personNames(JoinType.LEFT) {
                    or {
                        ilike("givenName", "%" + params.query + "%")
                        ilike("familyName", "%" + params.query + "%")
                    }
                }
                projections {
                    countDistinct("id")
                }
            }
        }
        // use withFormat here if we need to return xml in the future
        render(contentType: "application/json") {
            count = personCount
            results = personMap
        }
    }

    def show() {
        def personInstance = Person.get(params.id)
        render(contentType: "application/json") {
            convert(personInstance)
        }
    }

    @Transactional
    def save() {
        def person = request.JSON
        def personInstance = new Person(person)
        if (personInstance == null) {
            notFound()
            return
        }
        for (name in person["names"]) {
            def personNameInstance = new PersonName(name)
            personInstance.addToPersonNames(personNameInstance)
        }
        for (address in person["addresses"]) {
            def personAddressInstance = new PersonAddress(address)
            personInstance.addToPersonAddresses(personAddressInstance)
        }
        personInstance.save(flush: true, failOnError: true)
        response.status = CREATED.value();
        render(contentType: "application/json") {
            convert(personInstance)
        }
    }

    @Transactional
    def update() {
        def person = request.JSON
        def personInstance = Person.get(person["id"])
        if (personInstance == null) {
            notFound()
            return
        }
        personInstance.setGender(person["gender"])
        personInstance.setBirthdate(Date.parse("dd-MMM-yyyy", person["birthdate"]))
        for (personName in person["personNames"]) {
            def jsonPersonName = new PersonName(personName)
            if (personName["id"] != null) {
                for (personInstanceName in personInstance.personNames) {
                    if (personInstanceName.getId() == personName["id"]) {
                        personInstanceName.updatePersonName(jsonPersonName)
                    }
                }
            } else {
                personInstance.addToPersonNames(jsonPersonName)
            }
        }
        for (personAddress in person["personAddresses"]) {
            def jsonPersonAddress = new PersonAddress(personAddress)
            if (personAddress["id"] != null) {
                for (personInstanceAddress in personInstance.personAddresses) {
                    if (personInstanceAddress.getId() == personAddress["id"]) {
                        personInstanceAddress.updatePersonAddress(jsonPersonAddress)
                    }
                }
            } else {
                personInstance.addToPersonAddresses(jsonPersonAddress)
            }
        }
        personInstance.save(flush: true, failOnError: true)
        response.status = OK.value();
        render(contentType: "application/json") {
            convert(personInstance)
        }
    }

    @Transactional
    def delete(Person personInstance) {
        if (personInstance == null) {
            notFound()
            return
        }
        personInstance.delete flush: true
        render status: NO_CONTENT
    }

    protected void notFound() {
        render status: NOT_FOUND
    }
}
