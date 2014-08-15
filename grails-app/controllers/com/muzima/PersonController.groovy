package com.muzima

import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
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
                identifier     : personInstance.identifier,
                gender         : personInstance.gender,
                birthdate      : personInstance.birthdate.time,
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
        return person
    }

    @Secured(['isFullyAuthenticated()'])
    def index() {
        def personMap = []
        def personCount = 0
        if (params.query?.trim()) {
            Person.createCriteria().listDistinct() {
                firstResult: params.offset
                maxResults: params.max
                personNames() {
                    or {
                        ilike("givenName", "%" + params.query + "%")
                        ilike("familyName", "%" + params.query + "%")
                    }
                }
            }.each {
                personMap.add(convert(it))
            }
            personCount = Person.createCriteria().get {
                personNames() {
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

    @Secured(['isFullyAuthenticated()'])
    def show() {
        def personInstance = Person.get(params.id)
        render(contentType: "application/json") {
            convert(personInstance)
        }
    }

    @Secured(['isFullyAuthenticated()'])
    @Transactional
    def save() {
        def json = request.JSON
        def personInstance = new Person()
        personInstance.setGender(json["gender"])
        personInstance.setBirthdate(new Date(json["birthdate"]))

        Institution.all.each {
            // TODO: (hack) we're assuming we have only 1 institution for now in each installation.
            personInstance.setInstitution(it)
        }

        if (personInstance == null) {
            notFound()
            return
        }
        json["personNames"].each {
            def personNameInstance = new PersonName(it)
            personInstance.addToPersonNames(personNameInstance)
        }
        json["personAddresses"].each {
            def personAddressInstance = new PersonAddress(it)
            personInstance.addToPersonAddresses(personAddressInstance)
        }

        PersonName personName = personInstance.getPersonName()
        def identifier = personName.getGivenName().substring(0, 1) + personName.getFamilyName().substring(0, 1);
        int total = Person.countByIdentifierLike(identifier.toLowerCase() + "%")
        personInstance.setIdentifier(identifier.toLowerCase() + (total + 1))

        personInstance.save(flush: true, failOnError: true)
        response.status = CREATED.value()
        render(contentType: "application/json") {
            convert(personInstance)
        }
    }

    @Secured(['isFullyAuthenticated()'])
    @Transactional
    def update() {
        def json = request.JSON
        def personInstance = Person.get(json["id"])
        if (personInstance == null) {
            notFound()
            return
        }
        personInstance.setGender(json["gender"])
        personInstance.setBirthdate(new Date(json["birthdate"]))

        def personNames = personInstance.personNames
        def personAddresses = personInstance.personAddresses

        json["personNames"].each {
            def personNameId = it["id"]
            def personName = new PersonName(it)
            if (personNameId != null) {
                def personNameInstance = personNames.find({ it.id == personNameId })
                personNameInstance.updatePersonName(personName)
            } else {
                personInstance.addToPersonNames(personName)
            }
        }
        json["personAddresses"].each {
            def personAddressId = it["id"]
            def personAddress = new PersonAddress(it)
            if (personAddressId != null) {
                def personAddressInstance = personAddresses.find({ it.id == personAddressId })
                personAddressInstance.updatePersonAddress(personAddress)
            } else {
                personInstance.addToPersonAddresses(personAddress)
            }
        }
        personInstance.save(flush: true, failOnError: true)
        response.status = OK.value()
        render(contentType: "application/json") {
            convert(personInstance)
        }
    }

    protected void notFound() {
        render status: NOT_FOUND
    }
}
