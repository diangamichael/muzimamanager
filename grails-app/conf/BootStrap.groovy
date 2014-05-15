import com.muzima.Device
import com.muzima.DeviceDetail
import com.muzima.DeviceType
import com.muzima.Institution
import com.muzima.Person
import com.muzima.PersonAddress
import com.muzima.PersonName
import com.muzima.Role
import com.muzima.User
import com.muzima.UserRole

class BootStrap {

    def init = { servletContext ->

        def adminRole = new Role(authority: 'ROLE_ADMIN').save(flush: true)
        def userRole = new Role(authority: 'ROLE_USER').save(flush: true)
        assert Role.count() == 2

        def testUser1 = new User(username: 'me', password: 'password')
        testUser1.save(flush: true)

        def testUser2 = new User(username: 'you', password: 'password')
        testUser2.save(flush: true)
        assert User.count() == 2

        UserRole.create testUser1, adminRole, true
        UserRole.create testUser2, userRole, true
        assert UserRole.count() == 2

        def firstInstitution = new Institution(name: "First Institution", description: "This is the first institution.")
        firstInstitution.save(flush: true)

        def firstPersonName = new PersonName(givenName: "Family", familyName: "First")
        def firstPersonAddress = new PersonAddress(address1: "Address First Person")
        def firstPerson = new Person(gender: "F", birthdate: new Date().parse("dd/MM/yyyy", "28/09/2008"), institution: firstInstitution)
        firstPerson.addToPersonNames(firstPersonName)
        firstPerson.addToPersonAddresses(firstPersonAddress)
        firstPerson.save(flush: true, failOnError: true)


        def secondPersonName = new PersonName(givenName: "Family", familyName: "Second")
        def secondPersonSecondName = new PersonName(givenName: "Family", familyName: "Second", middleName: "Second")
        def secondPersonAddress = new PersonAddress(address1: "2201 W Bethel Ave", address2: "APT 77",
                cityVillage: "Muncie", countyDistrict: "Delaware", stateProvince: "IN", postalCode: "47304")
        def secondPersonSecondAddress = new PersonAddress(address1: "5442 Elkhorn Dr", address2: "APT 1123",
                cityVillage: "Indianapolis", countyDistrict: "Marion", stateProvince: "IN", postalCode: "46254")
        def secondPerson = new Person(gender: "M", birthdate: new Date().parse("dd/MM/yyyy", "28/09/2009"), institution: firstInstitution)
        secondPerson.addToPersonNames(secondPersonName)
        secondPerson.addToPersonNames(secondPersonSecondName)
        secondPerson.addToPersonAddresses(secondPersonAddress)
        secondPerson.addToPersonAddresses(secondPersonSecondAddress)
        secondPerson.save(flush: true, failOnError: true)

        def deviceTypeInstance = new DeviceType(name: "Nexus 5", description: "The Nexus 5 is an affordable device packing" +
                " top-shelf specs. Made in collaboration with Google and LG, the Nexus 5 runs on the newest Android" +
                " 4.4 KitKat. It is a solid performer featuring a Snapdragon 800 system chip and 2GB of RAM. It is" +
                " also the first Nexus to put the focus on camera performance as it comes with an 8-megapixel shooter" +
                " with optical image stabilization (OIS).")
        def deviceDetails =
                [["category": "Camera", "subCategory": "Camera", "categoryValue": "8 megapixels"],
                 ["category": "Camera", "subCategory": "Flash", "categoryValue": "LED"],
                 ["category": "Camera", "subCategory": "Aperture size", "categoryValue": "F2.4"],
                 ["category": "Camera", "subCategory": "Sensor size", "categoryValue": "1/3.4"],
                 ["category": "Camera", "subCategory": "Features", "categoryValue": "Back-illuminated sensor (BSI)," +
                         " Autofocus, Touch to focus, Optical image stabilization, Face detection, ISO control," +
                         " White balance presets, Burst mode, Digital zoom, Geo tagging, High Dynamic Range mode (HDR)," +
                         " Panorama, Scenes, Self-timer"],
                 ["category": "Camera", "subCategory": "Camcorder", "categoryValue": "1920x1080 (1080p HD) (30 fps)"],
                 ["category": "Camera", "subCategory": "Front facing camera", "categoryValue": "1.3 megapixels"],
                 ["category": "Technology", "subCategory": "CDMA", "categoryValue": "800, 1900 MHz"],
                 ["category": "Technology", "subCategory": "GSM", "categoryValue": "850, 900, 1800, 1900 MHz"],
                 ["category": "Technology", "subCategory": "UMTS", "categoryValue": "800, 850, 900, 1700/2100, 1900, 2100 MHz"],
                 ["category": "Technology", "subCategory": "FDD LTE", "categoryValue": "800 (band 20), 850 (band 5)," +
                         " 900 (band 8), 1800 (band 3), 2100 (band 1), 2600 (band 7) MHz"],
                 ["category": "Technology", "subCategory": "Micro Sim", "categoryValue": "Yes"],
                 ["category": "Technology", "subCategory": "Positioning", "categoryValue": "GPS, A-GPS, Glonass"],
                 ["category": "Technology", "subCategory": "Navigation", "categoryValue": "Turn by turn navigation"]
                ]
        for (deviceDetail in deviceDetails) {
            def deviceDetailInstance = new DeviceDetail(deviceDetail)
            deviceTypeInstance.addToDeviceDetails(deviceDetailInstance)
        }
        deviceTypeInstance.save(flush: true, failOnError: true)

        def firstDevice = new Device(imei: "000000000000000", sim: "15555215554", name: "HCT Device #001",
                description: "HCT device #001 description", status: "New", purchasedDate: new Date().parse("dd/MM/yyyy", "22/03/2013"),
                deviceType: deviceTypeInstance, institution: firstInstitution)
        firstDevice.save(flush: true, failOnError: true)

    }
    def destroy = {
    }
}
