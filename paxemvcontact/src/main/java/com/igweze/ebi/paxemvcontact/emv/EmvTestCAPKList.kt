package com.igweze.ebi.paxemvcontact.emv

import com.pax.jemv.clcommon.EMV_CAPK
import java.util.ArrayList

class EmvTestCAPKList(rid: String, keyId: Int, hashInd: Int, arithInd: Int,
                      module: String, exponent: String, expDate: String, checkSum: String) :
        EmvTestCAPK(0, rid, keyId, hashInd, arithInd, module, exponent, expDate, checkSum) {


    companion object {
        
        private val VISA_t90: EmvTestCAPK = EmvTestCAPKList(
                "A000000003", 0x90, 0x01, 0x01,
                "C26B3CB3833E42D8270DC10C8999B2DA18106838650DA0DBF154EFD51100AD144741B2A87D6881F8630E3348DEA3F78038E9B21A697EB2A6716D32CBF26086F1",
                "03", "151231",
                "B3AE2BC3CAFC05EEEFAA46A2A47ED51DE679F823")

        private val VISA_t92 = EmvTestCAPKList(
                "A000000003", 0x92, 0x01, 0x01,
                "996AF56F569187D09293C14810450ED8EE3357397B18A2458EFAA92DA3B6DF6514EC060195318FD43BE9B8F0CC669E3F844057CBDDF8BDA191BB64473BC8DC9A730DB8F6B4EDE3924186FFD9B8C7735789C23A36BA0B8AF65372EB57EA5D89E7D14E9C7B6B557460F10885DA16AC923F15AF3758F0F03EBD3C5C2C949CBA306DB44E6A2C076C5F67E281D7EF56785DC4D75945E491F01918800A9E2DC66F60080566CE0DAF8D17EAD46AD8E30A247C9F",
                "03", "151231",
                "429C954A3859CEF91295F663C963E582ED6EB253")

        private val VISA_t94 = EmvTestCAPKList(
                "A000000003", 0x94, 0x01, 0x01,
                "ACD2B12302EE644F3F835ABD1FC7A6F62CCE48FFEC622AA8EF062BEF6FB8BA8BC68BBF6AB5870EED579BC3973E121303D34841A796D6DCBC41DBF9E52C4609795C0CCF7EE86FA1D5CB041071ED2C51D2202F63F1156C58A92D38BC60BDF424E1776E2BC9648078A03B36FB554375FC53D57C73F5160EA59F3AFC5398EC7B67758D65C9BFF7828B6B82D4BE124A416AB7301914311EA462C19F771F31B3B57336000DFF732D3B83DE07052D730354D297BEC72871DCCF0E193F171ABA27EE464C6A97690943D59BDABB2A27EB71CEEBDAFA1176046478FD62FEC452D5CA393296530AA3F41927ADFE434A2DF2AE3054F8840657A26E0FC617",
                "03", "151231",
                "C4A3C43CCF87327D136B804160E47D43B60E6E0F")

        private val VISA_t95 = EmvTestCAPKList(
                "A000000003", 0x95, 0x01, 0x01,
                "BE9E1FA5E9A803852999C4AB432DB28600DCD9DAB76DFAAA47355A0FE37B1508AC6BF38860D3C6C2E5B12A3CAAF2A7005A7241EBAA7771112C74CF9A0634652FBCA0E5980C54A64761EA101A114E0F0B5572ADD57D010B7C9C887E104CA4EE1272DA66D997B9A90B5A6D624AB6C57E73C8F919000EB5F684898EF8C3DBEFB330C62660BED88EA78E909AFF05F6DA627B",
                "03", "151231",
                "EE1511CEC71020A9B90443B37B1D5F6E703030F6")

        private val VISA_t96 = EmvTestCAPKList(
                "A000000003", 0x96, 0x01, 0x01,
                "B74586D19A207BE6627C5B0AAFBC44A2ECF5A2942D3A26CE19C4FFAEEE920521868922E893E7838225A3947A2614796FB2C0628CE8C11E3825A56D3B1BBAEF783A5C6A81F36F8625395126FA983C5216D3166D48ACDE8A431212FF763A7F79D9EDB7FED76B485DE45BEB829A3D4730848A366D3324C3027032FF8D16A1E44D8D",
                "03", "151231",
                "7616E9AC8BE014AF88CA11A8FB17967B7394030E")

        // VISA 768 bits Test Key 97
        private val VISA_t97 = EmvTestCAPKList(
                "A000000003", 0x97, 0x01, 0x01,
                "AF0754EAED977043AB6F41D6312AB1E22A6809175BEB28E70D5F99B2DF18CAE73519341BBBD327D0B8BE9D4D0E15F07D36EA3E3A05C892F5B19A3E9D3413B0D97E7AD10A5F5DE8E38860C0AD004B1E06F4040C295ACB457A788551B6127C0B29",
                "03", "151231",
                "8001CA76C1203955E2C62841CD6F201087E564BF")

        // VISA 896 bits Test Key 98
        private val VISA_t98 = EmvTestCAPKList(
                "A000000003", 0x98, 0x01, 0x01,
                "CA026E52A695E72BD30AF928196EEDC9FAF4A619F2492E3FB31169789C276FFBB7D43116647BA9E0D106A3542E3965292CF77823DD34CA8EEC7DE367E08070895077C7EFAD939924CB187067DBF92CB1E785917BD38BACE0C194CA12DF0CE5B7A50275AC61BE7C3B436887CA98C9FD39",
                "03", "151231",
                "E7AC9AA8EED1B5FF1BD532CF1489A3E5557572C1")

        // VISA 1024 bits Test Key 99
        private val VISA_t99 = EmvTestCAPKList(
                "A000000003", 0x99, 0x01, 0x01,
                "AB79FCC9520896967E776E64444E5DCDD6E13611874F3985722520425295EEA4BD0C2781DE7F31CD3D041F565F747306EED62954B17EDABA3A6C5B85A1DE1BEB9A34141AF38FCF8279C9DEA0D5A6710D08DB4124F041945587E20359BAB47B7575AD94262D4B25F264AF33DEDCF28E09615E937DE32EDC03C54445FE7E382777",
                "03", "151231",
                "4ABFFD6B1C51212D05552E431C5B17007D2F5E6D")

        // Mastercard 1984 bits transaction time test Key EF
        private val MASTER_tEF = EmvTestCAPKList(
                "A000000004", 0xEF, 0x01, 0x01,
                "A191CB87473F29349B5D60A88B3EAEE0973AA6F1A082F358D849FDDFF9C091F899EDA9792CAF09EF28F5D22404B88A2293EEBBC1949C43BEA4D60CFD879A1539544E09E0F09F60F065B2BF2A13ECC705F3D468B9D33AE77AD9D3F19CA40F23DCF5EB7C04DC8F69EBA565B1EBCB4686CD274785530FF6F6E9EE43AA43FDB02CE00DAEC15C7B8FD6A9B394BABA419D3F6DC85E16569BE8E76989688EFEA2DF22FF7D35C043338DEAA982A02B866DE5328519EBBCD6F03CDD686673847F84DB651AB86C28CF1462562C577B853564A290C8556D818531268D25CC98A4CC6A0BDFFFDA2DCCA3A94C998559E307FDDF915006D9A987B07DDAEB3B",
                "03", "151231",
                "21766EBB0EE122AFB65D7845B73DB46BAB65427A")

        // Mastercard 1664 bits transaction time test Key F0
        private val MASTER_tF0 = EmvTestCAPKList(
                "A000000004", 0xF0, 0x01, 0x01,
                "999EA2D430D60614E100706C7DA213E1C77AD18C11BD70BC42CEBD80A3C94EC5E736D345EA7ADE2B9E0BC8816E567D39412EB728C2B2CCE73DEBC9FA25D4919BF5420C986083FBC0750895AFBA6B9DAA62B1B7D8439CF29E720D085D5D0962A9443B1F738E6560EF0EED7572815EA87A1B07570F119867DD6CC5D4DE06AA5373847D17A610ECF932FA2C94234E68AF84A9E0DAA18116B326016B70136F493482FEAE98E4AE682BF96C59279752248DEC915ED6F9BB73F9206155D961B50865E1CA6D47322FCE22DCF1957182B6E99CBB",
                "03", "151231",
                "B8EA49169B54F3B7FF0DF3A8B6388C82A1DBE730")

        // Mastercard 1408 bits transaction time test Key F1
        private val MASTER_tF1 = EmvTestCAPKList(
                "A000000004", 0xF1, 0x01, 0x01,
                "A0DCF4BDE19C3546B4B6F0414D174DDE294AABBB828C5A834D73AAE27C99B0B053A90278007239B6459FF0BBCD7B4B9C6C50AC02CE91368DA1BD21AAEADBC65347337D89B68F5C99A09D05BE02DD1F8C5BA20E2F13FB2A27C41D3F85CAD5CF6668E75851EC66EDBF98851FD4E42C44C1D59F5984703B27D5B9F21B8FA0D93279FBBF69E090642909C9EA27F898959541AA6757F5F624104F6E1D3A9532F2A6E51515AEAD1B43B3D7835088A2FAFA7BE7",
                "03", "151231",
                "D8E68DA167AB5A85D8C3D55ECB9B0517A1A5B4BB")

        // Mastercard 1152 bits transaction time test Key F2 (exponent=65537)
        private val MASTER_tF2 = EmvTestCAPKList(
                "A000000004", 0xF2, 0x01, 0x01,
                "A2B9FF84F87FA108FF9A8B2E93FD5A37CBFDA184F189CEB3763090319CABBDD822EC4011EDA36989E5D0680666C225FC3E83FF0996D23E0F94F9F65D0FC21C3929B08E2FCFB6F5826020CF965050B0381D9B47BD930B9346A7E192B6FFB71BF458585E844FE504741A04C3DEFB1DC84CDDDE3F6686D622AEE3216E45FB77E7E4E48F5F3D8F9D9582685FD099CBD62873",
                "010001", "151231",
                "0F3BA8CB5777DC4AA96C30BFB1FC267A382A4847")

        // Mastercard 1152 bits transaction time test Key F3
        private val MASTER_tF3 = EmvTestCAPKList(
                "A000000004", 0xF3, 0x01, 0x01,
                "98F0C770F23864C2E766DF02D1E833DFF4FFE92D696E1642F0A88C5694C6479D16DB1537BFE29E4FDC6E6E8AFD1B0EB7EA0124723C333179BF19E93F10658B2F776E829E87DAEDA9C94A8B3382199A350C077977C97AFF08FD11310AC950A72C3CA5002EF513FCCC286E646E3C5387535D509514B3B326E1234F9CB48C36DDD44B416D23654034A66F403BA511C5EFA3",
                "03", "151231",
                "A69AC7603DAF566E972DEDC2CB433E07E8B01A9A")

        // Mastercard 896 bits transaction time test Key F4
        private val MASTER_tF4 = EmvTestCAPKList(
                "A000000004", 0xF4, 0x01, 0x01,
                "9CFAD54B40297C1CDE23FCB3EF68D318341A4727AE1DAA2BEBE35872EF3DC90746297B066ED1CE3C07C1F234FF5490425E8B14674CC57E4397A51584FF5EBA6B5D54D99D2C9FC99D5E4CACB3487ABA790F28E304987AFA7F5F92E22D89FF510C1B581941166C7CCB11EFB08DE607460D",
                "03", "151231",
                "98CEB9E0E8ED52ABDD8549FD50ACECA3BF51A786")

        // Mastercard 1024 bits transaction time test Key F8 (also for ETEC test)
        private val MASTER_tF8 = EmvTestCAPKList(
                "A000000004", 0xF8, 0x01, 0x01,
                "A1F5E1C9BD8650BD43AB6EE56B891EF7459C0A24FA84F9127D1A6C79D4930F6DB1852E2510F18B61CD354DB83A356BD190B88AB8DF04284D02A4204A7B6CB7C5551977A9B36379CA3DE1A08E69F301C95CC1C20506959275F41723DD5D2925290579E5A95B0DF6323FC8E9273D6F849198C4996209166D9BFC973C361CC826E1",
                "03", "151231",
                "F06ECC6D2AAEBF259B7E755A38D9A9B24E2FF3DD")

        // VISA 1024 bits Live Key 01
        private val VISA_v01 = EmvTestCAPKList(
                "A000000003", 0x01, 0x01, 0x01,
                "C696034213D7D8546984579D1D0F0EA519CFF8DEFFC429354CF3A871A6F7183F1228DA5C7470C055387100CB935A712C4E2864DF5D64BA93FE7E63E71F25B1E5F5298575EBE1C63AA617706917911DC2A75AC28B251C7EF40F2365912490B939BCA2124A30A28F54402C34AECA331AB67E1E79B285DD5771B5D9FF79EA630B75",
                "03", "091231",
                "D34A6A776011C7E7CE3AEC5F03AD2F8CFC5503CC")


        // VISA 1152 bits Live Key 07
        private val VISA_v07 = EmvTestCAPKList(
                "A000000003", 0x07, 0x01, 0x01,
                "A89F25A56FA6DA258C8CA8B40427D927B4A1EB4D7EA326BBB12F97DED70AE5E4480FC9C5E8A972177110A1CC318D06D2F8F5C4844AC5FA79A4DC470BB11ED635699C17081B90F1B984F12E92C1C529276D8AF8EC7F28492097D8CD5BECEA16FE4088F6CFAB4A1B42328A1B996F9278B0B7E3311CA5EF856C2F888474B83612A82E4E00D0CD4069A6783140433D50725F",
                "03", "121231",
                "B4BC56CC4E88324932CBC643D6898F6FE593B172")
//Modified from old 1152 bits key to new one on Feb 6,2004.

        // VISA 1408 BITS LIVE KEY 08
        private val VISA_v08 = EmvTestCAPKList(
                "A000000003", 0x08, 0x01, 0x01,
                "D9FD6ED75D51D0E30664BD157023EAA1FFA871E4DA65672B863D255E81E137A51DE4F72BCC9E44ACE12127F87E263D3AF9DD9CF35CA4A7B01E907000BA85D24954C2FCA3074825DDD4C0C8F186CB020F683E02F2DEAD3969133F06F7845166ACEB57CA0FC2603445469811D293BFEFBAFAB57631B3DD91E796BF850A25012F1AE38F05AA5C4D6D03B1DC2E568612785938BBC9B3CD3A910C1DA55A5A9218ACE0F7A21287752682F15832A678D6E1ED0B",
                "03", "141231",
                "20D213126955DE205ADC2FD2822BD22DE21CF9A8")
// expired on December 31, 2014

        // VISA 1984 BITS LIVE KEY 09
        private val VISA_v09 = EmvTestCAPKList(
                "A000000003", 0x09, 0x01, 0x01,
                "9D912248DE0A4E39C1A7DDE3F6D2588992C1A4095AFBD1824D1BA74847F2BC4926D2EFD904B4B54954CD189A54C5D1179654F8F9B0D2AB5F0357EB642FEDA95D3912C6576945FAB897E7062CAA44A4AA06B8FE6E3DBA18AF6AE3738E30429EE9BE03427C9D64F695FA8CAB4BFE376853EA34AD1D76BFCAD15908C077FFE6DC5521ECEF5D278A96E26F57359FFAEDA19434B937F1AD999DC5C41EB11935B44C18100E857F431A4A5A6BB65114F174C2D7B59FDF237D6BB1DD0916E644D709DED56481477C75D95CDD68254615F7740EC07F330AC5D67BCD75BF23D28A140826C026DBDE971A37CD3EF9B8DF644AC385010501EFC6509D7A41",
                "03", "161231",
                "1FF80A40173F52D7D27E0F26A146A1C8CCB29046")

        // Mastercard 1024 bits Live Key 03
        private val MASTER_v03 = EmvTestCAPKList(
                "A000000004", 0x03, 0x01, 0x01,
                "C2490747FE17EB0584C88D47B1602704150ADC88C5B998BD59CE043EDEBF0FFEE3093AC7956AD3B6AD4554C6DE19A178D6DA295BE15D5220645E3C8131666FA4BE5B84FE131EA44B039307638B9E74A8C42564F892A64DF1CB15712B736E3374F1BBB6819371602D8970E97B900793C7C2A89A4A1649A59BE680574DD0B60145",
                "03", "091231",
                "5ADDF21D09278661141179CBEFF272EA384B13BB")

        // Mastercard 1152 bits Live Key 04
        private val MASTER_v04 = EmvTestCAPKList(
                "A000000004", 0x04, 0x01, 0x01,
                "A6DA428387A502D7DDFB7A74D3F412BE762627197B25435B7A81716A700157DDD06F7CC99D6CA28C2470527E2C03616B9C59217357C2674F583B3BA5C7DCF2838692D023E3562420B4615C439CA97C44DC9A249CFCE7B3BFB22F68228C3AF13329AA4A613CF8DD853502373D62E49AB256D2BC17120E54AEDCED6D96A4287ACC5C04677D4A5A320DB8BEE2F775E5FEC5",
                "03", "121231",
                "381A035DA58B482EE2AF75F4C3F2CA469BA4AA6C")

        // Mastercard 1408 bits live Key 05
        private val MASTER_v05 = EmvTestCAPKList(
                "A000000004", 0x05, 0x01, 0x01,
                "B8048ABC30C90D976336543E3FD7091C8FE4800DF820ED55E7E94813ED00555B573FECA3D84AF6131A651D66CFF4284FB13B635EDD0EE40176D8BF04B7FD1C7BACF9AC7327DFAA8AA72D10DB3B8E70B2DDD811CB4196525EA386ACC33C0D9D4575916469C4E4F53E8E1C912CC618CB22DDE7C3568E90022E6BBA770202E4522A2DD623D180E215BD1D1507FE3DC90CA310D27B3EFCCD8F83DE3052CAD1E48938C68D095AAC91B5F37E28BB49EC7ED597",
                "03", "141231",
                "EBFA0D5D06D8CE702DA3EAE890701D45E274C845")

        // Mastercard 1948 bits live Key 06
        private val MASTER_v06 = EmvTestCAPKList(
                "A000000004", 0x06, 0x01, 0x01,
                "CB26FC830B43785B2BCE37C81ED334622F9622F4C89AAE641046B2353433883F307FB7C974162DA72F7A4EC75D9D657336865B8D3023D3D645667625C9A07A6B7A137CF0C64198AE38FC238006FB2603F41F4F3BB9DA1347270F2F5D8C606E420958C5F7D50A71DE30142F70DE468889B5E3A08695B938A50FC980393A9CBCE44AD2D64F630BB33AD3F5F5FD495D31F37818C1D94071342E07F1BEC2194F6035BA5DED3936500EB82DFDA6E8AFB655B1EF3D0D7EBF86B66DD9F29F6B1D324FE8B26CE38AB2013DD13F611E7A594D675C4432350EA244CC34F3873CBA06592987A1D7E852ADC22EF5A2EE28132031E48F74037E3B34AB747F",
                "03", "161231",
                "F910A1504D5FFB793D94F3B500765E1ABCAD72D9")


        fun genCapks(): List<EMV_CAPK?> {
            val capks = ArrayList<EMV_CAPK?>()
            capks.add(toCapk(VISA_v01))
            capks.add(toCapk(VISA_v07))
            capks.add(toCapk(VISA_v08))
            capks.add(toCapk(VISA_v09))
            capks.add(toCapk(VISA_t90))
            capks.add(toCapk(VISA_t92))
            capks.add(toCapk(VISA_t94))
            capks.add(toCapk(VISA_t95))
            capks.add(toCapk(VISA_t96))
            capks.add(toCapk(VISA_t97))
            capks.add(toCapk(VISA_t98))
            capks.add(toCapk(VISA_t99))
            capks.add(toCapk(VISA_t92))

            capks.add(toCapk(MASTER_v03))
            capks.add(toCapk(MASTER_v04))
            capks.add(toCapk(MASTER_v05))
            capks.add(toCapk(MASTER_v06))
            capks.add(toCapk(MASTER_tEF))
            capks.add(toCapk(MASTER_tF1))
            capks.add(toCapk(MASTER_tF2))
            capks.add(toCapk(MASTER_tF3))
            capks.add(toCapk(MASTER_tF4))
            capks.add(toCapk(MASTER_tF8))

            return capks
        }
    }

}