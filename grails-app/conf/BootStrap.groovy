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

        def testUser1 = new User(username: 'root', password: 'password')
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
        def firstPerson = new Person(identifier: "ff1", gender: "F", birthdate: new Date().parse("dd/MM/yyyy", "28/09/2008"), institution: firstInstitution)
        firstPerson.addToPersonNames(firstPersonName)
        firstPerson.addToPersonAddresses(firstPersonAddress)
        firstPerson.save(flush: true, failOnError: true)
        testUser1.setPerson(firstPerson)
        testUser1.save(flush: true)

        def secondPersonName = new PersonName(givenName: "Family", familyName: "Second")
        def secondPersonSecondName = new PersonName(givenName: "Family", familyName: "Second", middleName: "Second")
        def secondPersonAddress = new PersonAddress(address1: "2201 W Bethel Ave", address2: "APT 77",
                cityVillage: "Muncie", countyDistrict: "Delaware", stateProvince: "IN", postalCode: "47304")
        def secondPersonSecondAddress = new PersonAddress(address1: "5442 Elkhorn Dr", address2: "APT 1123",
                cityVillage: "Indianapolis", countyDistrict: "Marion", stateProvince: "IN", postalCode: "46254")
        def secondPerson = new Person(identifier: "fs1", gender: "M", birthdate: new Date().parse("dd/MM/yyyy", "28/09/2009"), institution: firstInstitution)
        secondPerson.addToPersonNames(secondPersonName)
        secondPerson.addToPersonNames(secondPersonSecondName)
        secondPerson.addToPersonAddresses(secondPersonAddress)
        secondPerson.addToPersonAddresses(secondPersonSecondAddress)
        secondPerson.save(flush: true, failOnError: true)
        testUser2.setPerson(secondPerson)
        testUser2.save(flush: true)

        def nexusOneType = new DeviceType(name: "Nexus One", description: "The Nexus One (codenamed HTC " +
                "Passion) is an Android smartphone designed and manufactured by HTC as Google's first Google Nexus " +
                "smartphone. The Nexus One became available on January 5, 2010, and features the ability to " +
                "transcribe voice to text, an additional microphone for dynamic noise suppression, and voice guided " +
                "turn-by-turn navigation to drivers.\n\n" +
                "The device was sold SIM unlocked and not restricted to use on a single network provider. Google " +
                "offered T-Mobile US and AT&T versions of the phone online in the United States before closing the " +
                "online store in July 2010. A version for use on Vodafone (European) networks was announced on April " +
                "26, 2010, available in the UK on April 30, 2010. On March 16, 2010, the Nexus One device became " +
                "available on the Google web store for sale in Canada for use with most Canadian carriers. In May " +
                "2010 Google announced the closing of the web store, with the intention to distribute the phone " +
                "through partners around the world.\n\n" +
                "Copied from wikipedia of Nexus One.")
        def nexusOneDetails =
                [["category": "General", "subCategory": "2G Network", "categoryValue": "GSM 850 / 900 / 1800 / 1900"],
                 ["category": "General", "subCategory": "3G Network", "categoryValue": "HSDPA 900 / 1700 / 2100"],
                 ["category": "General", "subCategory": "SIM", "categoryValue": "Mini-SIM"],
                 ["category": "General", "subCategory": "Released", "categoryValue": "2010, January"],
                 ["category": "Body", "subCategory": "Dimensions", "categoryValue": "119 x 59.8 x 11.5 mm (4.69 x 2.35 x 0.45 in)"],
                 ["category": "Body", "subCategory": "Weight", "categoryValue": "130 g (4.59 oz)"],
                 ["category": "Display", "subCategory": "Type", "categoryValue": "AMOLED capacitive touchscreen, 16M colors"],
                 ["category": "Display", "subCategory": "Size", "categoryValue": "480 x 800 pixels, 3.7 inches (~252 ppi pixel density)"],
                 ["category": "Display", "subCategory": "Multitouch", "categoryValue": "Yes"],
                 ["category": "Display", "subCategory": "Trackball navigation", "categoryValue": "Yes"],
                 ["category": "Sound", "subCategory": "Alert types", "categoryValue": "Vibration, MP3 ringtones"],
                 ["category": "Sound", "subCategory": "Loudspeaker", "categoryValue": "Yes"],
                 ["category": "Sound", "subCategory": "3.5mm jack", "categoryValue": "Yes"],
                 ["category": "Memory", "subCategory": "Card slot", "categoryValue": "microSD, up to 32 GB, 4 GB included"],
                 ["category": "Memory", "subCategory": "Internal", "categoryValue": "512 MB RAM, 512 MB ROM"],
                 ["category": "Data", "subCategory": "GPRS", "categoryValue": "Class 10 (4+1/3+2 slots), 32 48 kbps"],
                 ["category": "Data", "subCategory": "EDGE", "categoryValue": "Class 10, 236.8 kbps"],
                 ["category": "Data", "subCategory": "Speed", "categoryValue": "HSDPA 7.2 Mbps; HSUPA, 2 Mbps"],
                 ["category": "Data", "subCategory": "WLAN", "categoryValue": "Wi-Fi 802.11 a/b/g"],
                 ["category": "Data", "subCategory": "Bluetooth", "categoryValue": "Yes, v2.1 with A2DP"],
                 ["category": "Data", "subCategory": "USB", "categoryValue": "Yes, microUSB v2.0"],
                 ["category": "Camera", "subCategory": "Primary", "categoryValue": "5 MP, 2560 x 1920 pixels, autofocus, LED flash, check quality"],
                 ["category": "Camera", "subCategory": "Features", "categoryValue": "Geo-tagging"],
                 ["category": "Camera", "subCategory": "Video", "categoryValue": "Yes, 480p@24fps"],
                 ["category": "Camera", "subCategory": "Secondary", "categoryValue": "No"],
                 ["category": "Features", "subCategory": "OS", "categoryValue": "Android OS, v2.1 (Eclair), upgradable to v2.3.6 (Gingerbread)"],
                 ["category": "Features", "subCategory": "Chipset", "categoryValue": "Qualcomm QSD8250 Snapdragon"],
                 ["category": "Features", "subCategory": "CPU", "categoryValue": "1 GHz Scorpion"],
                 ["category": "Features", "subCategory": "GPU", "categoryValue": "Adreno 200"],
                 ["category": "Features", "subCategory": "Sensors", "categoryValue": "Accelerometer, proximity, compass"],
                 ["category": "Features", "subCategory": "Messaging", "categoryValue": "SMS(threaded view), MMS, Email, Push Email, IM"],
                 ["category": "Features", "subCategory": "Browser", "categoryValue": "HTML"],
                 ["category": "Features", "subCategory": "Radio", "categoryValue": "Factory locked by default, can be enabled"],
                 ["category": "Features", "subCategory": "GPS", "categoryValue": "Yes, with A-GPS support"],
                 ["category": "Features", "subCategory": "Java", "categoryValue": "Yes, via Java MIDP emulator"],
                 ["category": "Features", "subCategory": "Colors", "categoryValue": "Brown (teflon coating)"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": "Active noise cancellation with dedicated microphone"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": "Dedicated search key"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": "Google Search, Maps, Gmail"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": "YouTube, Google Talk, Picasa"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": "MP3/eAAC+/WAV music player"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": "MP4/H.263/H.264 video player"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": "Voice memo"],
                 ["category": "Battery", "subCategory": "Type", "categoryValue": "Li-Ion 1400 mAh battery"],
                 ["category": "Battery", "subCategory": "Stand-by", "categoryValue": "Up to 290 h (2G) / Up to 250 h (3G)"],
                 ["category": "Battery", "subCategory": "Talk time", "categoryValue": "Up to 10 hours (2G) / Up to 7 hours (3G)"],
                 ["category": "Battery", "subCategory": "Music play", "categoryValue": "Up to 20 hours"]]
        for (nexusOneDetail in nexusOneDetails) {
            def nexusOneDetailInstance = new DeviceDetail(nexusOneDetail)
            nexusOneType.addToDeviceDetails(nexusOneDetailInstance)
        }
        nexusOneType.save(flush: true, failOnError: true)

        def galaxyTabType = new DeviceType(name: "Galaxy Tab 2", description: "The Samsung Galaxy Tab 2 7.0 " +
                "is a 7-inch Android tablet produced and marketed by Samsung Electronics.Samsung Galaxy Tab 2 is Ice " +
                "Cream Sandwich sequel It belongs to the second generation of the Samsung Galaxy Tab series, which " +
                "also includes a 10.1-inch model, the Galaxy Tab 2 10.1. It was announced on 13 February 2012, and " +
                "launched in the US on 22 April 2012. It is the successor to the Samsung Galaxy Tab 7.0 Plus.\n\n" +
                "Copied from wikipedia of Samsung Galaxy Tab 2.")
        def galaxyTabDetails =
                [["category": "General", "subCategory": "2G Network", "categoryValue": "GSM 850 / 900 / 1800 / 1900"],
                 ["category": "General", "subCategory": "3G Network", "categoryValue": "HSDPA 900 / 2100 - GT-P3100"],
                 ["category": "General", "subCategory": "SIM", "categoryValue": "Mini-SIM"],
                 ["category": "General", "subCategory": "Released", "categoryValue": "2012, February"],
                 ["category": "Body", "subCategory": "Dimensions", "categoryValue": "193.7 x 122.4 x 10.5 mm (7.63 x 4.82 x 0.41 in)"],
                 ["category": "Body", "subCategory": "Weight", "categoryValue": "345 g (12.17 oz)"],
                 ["category": "Display", "subCategory": "Type", "categoryValue": "PLS LCD capacitive touchscreen, 16M colors"],
                 ["category": "Display", "subCategory": "Size", "categoryValue": "600 x 1024 pixels, 7.0 inches (~170 ppi pixel density)"],
                 ["category": "Display", "subCategory": "Multitouch", "categoryValue": "Yes"],
                 ["category": "Display", "subCategory": "Other", "categoryValue": "- TouchWiz UX UI"],
                 ["category": "Sound", "subCategory": "Alert types", "categoryValue": "Vibration; MP3, WAV ringtones"],
                 ["category": "Sound", "subCategory": "Loudspeaker", "categoryValue": "Yes, with stereo speakers"],
                 ["category": "Sound", "subCategory": "3.5mm jack", "categoryValue": "Yes"],
                 ["category": "Memory", "subCategory": "Card slot", "categoryValue": "microSD, up to 32 GB"],
                 ["category": "Memory", "subCategory": "Internal", "categoryValue": "8/16/32 GB storage, 1 GB RAM"],
                 ["category": "Data", "subCategory": "GPRS", "categoryValue": "Class 33"],
                 ["category": "Data", "subCategory": "EDGE", "categoryValue": "Yes"],
                 ["category": "Data", "subCategory": "Speed", "categoryValue": "HSDPA, 21 Mbps; HSUPA, 5.76 Mbps"],
                 ["category": "Data", "subCategory": "WLAN", "categoryValue": "Wi-Fi 802.11 b/g/n, Wi-Fi Direct, Wi-Fi hotspot"],
                 ["category": "Data", "subCategory": "Bluetooth", "categoryValue": "Yes, v3.0 with A2DP, HS"],
                 ["category": "Data", "subCategory": "USB", "categoryValue": "Yes, v2.0, USB Host"],
                 ["category": "Camera", "subCategory": "Primary", "categoryValue": "3.15 MP, 2048 x 1536 pixels"],
                 ["category": "Camera", "subCategory": "Features", "categoryValue": "Geo-tagging, smile detection"],
                 ["category": "Camera", "subCategory": "Video", "categoryValue": "Yes, 720p@30fps"],
                 ["category": "Camera", "subCategory": "Secondary", "categoryValue": "Yes, VGA"],
                 ["category": "Features", "subCategory": "OS", "categoryValue": "Android OS, v4.0.3 (Ice Cream Sandwich), v4.1.1, upgradable to v4.2.2 (Jelly Bean)"],
                 ["category": "Features", "subCategory": "Chipset", "categoryValue": "TI OMAP 4430"],
                 ["category": "Features", "subCategory": "CPU", "categoryValue": "Dual-core 1 GHz"],
                 ["category": "Features", "subCategory": "GPU", "categoryValue": "PowerVR SGX540"],
                 ["category": "Features", "subCategory": "Sensors", "categoryValue": "Accelerometer, gyro, proximity, compass"],
                 ["category": "Features", "subCategory": "Messaging", "categoryValue": "SMS(threaded view), MMS, Email, Push Email, IM, RSS"],
                 ["category": "Features", "subCategory": "Browser", "categoryValue": "HTML5, Adobe Flash"],
                 ["category": "Features", "subCategory": "Radio", "categoryValue": "No"],
                 ["category": "Features", "subCategory": "GPS", "categoryValue": "Yes, with A-GPS support and GLONASS"],
                 ["category": "Features", "subCategory": "Java", "categoryValue": "Yes, via Java MIDP emulator"],
                 ["category": "Features", "subCategory": "Colors", "categoryValue": "Black, White, Red"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": " SNS integration"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": " MP4/DivX/H.264/H.263/WMV player"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": " MP3/WAV/eAAC+/WMA/Flac player"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": " Organizer"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": " Image/video editor"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": " Document viewer"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": " Google Search, Maps, Gmail,"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": "YouTube, Calendar, Google Talk, Picasa"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": " Voice memo"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": " Predictive text input (Swype)"],
                 ["category": "Battery", "subCategory": "Other", "categoryValue": "Non-removable Li-Ion 4000 mAh battery"],
                 ["category": "Battery", "subCategory": "Stand-by", "categoryValue": "Up to 1190 h (2G) / Up to 1080 h (3G)"],
                 ["category": "Battery", "subCategory": "Talk time", "categoryValue": "Up to 40 h (2G) / Up to 20 h (3G)"]]
        for (galaxyTabDetail in galaxyTabDetails) {
            def galaxyTabDetailInstance = new DeviceDetail(galaxyTabDetail)
            galaxyTabType.addToDeviceDetails(galaxyTabDetailInstance)
        }
        galaxyTabType.save(flush: true, failOnError: true)

        def huaweiAscendType = new DeviceType(name: "Ascend Y210", description: "The Huawei Ascend is a Windows Phone " +
                "and Android smartphone series manufactured by Huawei.\n\n" +
                "Copied from wikipedia of Huawei Ascend Y210.")
        def huaweiAscendDetails =
                [["category": "General", "subCategory": "2G Network", "categoryValue": "GSM 850 / 900 / 1800 / 1900 - SIM 1 & SIM 2"],
                 ["category": "General", "subCategory": "3G Network", "categoryValue": "HSDPA 900 / 2100 - Y210-0200 model"],
                 ["category": "General", "subCategory": "SIM", "categoryValue": "Dual SIM (Mini-SIM, dual stand-by)"],
                 ["category": "General", "subCategory": "Released", "categoryValue": "2013, March"],
                 ["category": "Body", "subCategory": "Dimensions", "categoryValue": "117 x 62 x 12.4 mm (4.61 x 2.44 x 0.49 in)"],
                 ["category": "Body", "subCategory": "Weight", "categoryValue": "120 g (4.23 oz)"],
                 ["category": "Display", "subCategory": "Type", "categoryValue": "TFT capacitive touchscreen, 256K colors"],
                 ["category": "Display", "subCategory": "Size", "categoryValue": "320 x 480 pixels, 3.5 inches (~165 ppi pixel density)"],
                 ["category": "Display", "subCategory": "Multitouch", "categoryValue": "Yes"],
                 ["category": "Sound", "subCategory": "Alert types", "categoryValue": "Vibration, MP3 ringtones"],
                 ["category": "Sound", "subCategory": "Loudspeaker", "categoryValue": "Yes"],
                 ["category": "Sound", "subCategory": "3.5mm jack", "categoryValue": "Yes"],
                 ["category": "Memory", "subCategory": "Card slot", "categoryValue": "microSD, up to 32 GB"],
                 ["category": "Memory", "subCategory": "Internal", "categoryValue": "512 MB ROM, 256 MB RAM"],
                 ["category": "Data", "subCategory": "GPRS", "categoryValue": "Yes"],
                 ["category": "Data", "subCategory": "EDGE", "categoryValue": "Yes"],
                 ["category": "Data", "subCategory": "Speed", "categoryValue": "HSDPA, 7.2 Mbps; HSUPA"],
                 ["category": "Data", "subCategory": "WLAN", "categoryValue": "Wi-Fi 802.11 b/g/n, Wi-Fi hotspot"],
                 ["category": "Data", "subCategory": "Bluetooth", "categoryValue": "Yes, v2.1 with EDR"],
                 ["category": "Data", "subCategory": "USB", "categoryValue": "Yes, microUSB v2.0"],
                 ["category": "Camera", "subCategory": "Primary", "categoryValue": "2 MP, 1600 x 1200 pixels"],
                 ["category": "Camera", "subCategory": "Features", "categoryValue": "Geo-tagging"],
                 ["category": "Camera", "subCategory": "Video", "categoryValue": "Yes, VGA@30fps"],
                 ["category": "Camera", "subCategory": "Secondary", "categoryValue": "No"],
                 ["category": "Features", "subCategory": "OS", "categoryValue": "Android OS, v2.3.6 (Gingerbread)"],
                 ["category": "Features", "subCategory": "Chipset", "categoryValue": "Qualcomm MSM7225A Snapdragon"],
                 ["category": "Features", "subCategory": "CPU", "categoryValue": "1 GHz Cortex-A5"],
                 ["category": "Features", "subCategory": "GPU", "categoryValue": "Adreno 200"],
                 ["category": "Features", "subCategory": "Sensors", "categoryValue": "Accelerometer"],
                 ["category": "Features", "subCategory": "Messaging", "categoryValue": "SMS(threaded view), MMS, Email, IM"],
                 ["category": "Features", "subCategory": "Browser", "categoryValue": "HTML"],
                 ["category": "Features", "subCategory": "Radio", "categoryValue": "No"],
                 ["category": "Features", "subCategory": "GPS", "categoryValue": "Yes, with A-GPS support"],
                 ["category": "Features", "subCategory": "Java", "categoryValue": "Yes, via Java MIDP emulator"],
                 ["category": "Features", "subCategory": "Colors", "categoryValue": "Black"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": "SNS integration"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": " MP3/WAV/eAAC+ player"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": " MP4/H.264 player"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": " Organizer"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": " Document viewer"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": " Photo viewer/editor"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": " Voice memo/dial"],
                 ["category": "Features", "subCategory": "Other", "categoryValue": " Predictive text input"],
                 ["category": "Battery", "subCategory": "Type", "categoryValue": "Li-Ion 1700 mAh battery"]]
        for (huaweiAscendDetail in huaweiAscendDetails) {
            def huaweiAscendDetailInstance = new DeviceDetail(huaweiAscendDetail)
            huaweiAscendType.addToDeviceDetails(huaweiAscendDetailInstance)
        }
        huaweiAscendType.save(flush: true, failOnError: true)

        def firstDevice = new Device(imei: "000000000000000", sim: "15555215554", name: "HCT Device #001",
                description: "HCT device #001 description", status: "New", purchasedDate: new Date().parse("dd/MM/yyyy", "22/03/2013"),
                deviceType: nexusOneType, institution: firstInstitution)
        firstDevice.save(flush: true, failOnError: true)

    }
    def destroy = {
    }
}
