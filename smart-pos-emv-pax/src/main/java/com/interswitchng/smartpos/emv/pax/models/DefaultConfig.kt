package com.interswitchng.smartpos.emv.pax.models

internal fun getDefaultEmvConfig(): EmvAIDs {

    return aid {

        //  Verve
        card(this) {
            name = "Verve"
            aid = "A0000003710001"
            partialMatch = true

            key(this) {
                id = "05"
                expiry = "230228"
                modulus = "B036A8CAE0593A480976BFE84F8A67759E52B3D9F4A68CCC37FE720E594E5694CD1AE20E1B120D7A18FA5C70E044D3B12E932C9BBD9FDEA4BE11071EF8CA3AF48FF2B5DDB307FC752C5C73F5F274D4238A92B4FCE66FC93DA18E6C1CC1AA3CFAFCB071B67DAACE96D9314DB494982F5C967F698A05E1A8A69DA931B8E566270F04EAB575F5967104118E4F12ABFF9DEC92379CD955A10675282FE1B60CAD13F9BB80C272A40B6A344EA699FB9EFA6867"
                exponent = "03"
                checksum = "676822D335AB0D2C3848418CB546DF7B6A6C32C0"
            }
        }

        // China Union Pay
        card(this) {
            name = "CUP"
            aid = "A000000333010102"
            partialMatch = true

            key(this) {
                id = "08"
                expiry = "301231"
                modulus = "B61645EDFD5498FB246444037A0FA18C0F101EBD8EFA54573CE6E6A7FBF63ED21D66340852B0211CF5EEF6A1CD989F66AF21A8EB19DBD8DBC3706D135363A0D683D046304F5A836BC1BC632821AFE7A2F75DA3C50AC74C545A754562204137169663CFCC0B06E67E2109EBA41BC67FF20CC8AC80D7B6EE1A95465B3B2657533EA56D92D539E5064360EA4850FED2D1BF"
                exponent = "03"
                checksum = "EE23B616C95C02652AD18860E48787C079E8E85A"
            }

            key(this) {
                id = "12"
                expiry = "301231"
                modulus = "DED9E1BC8E749CAD749484BFB472445BC81FFAA89707648C342AA30D1BE60D5ED0F6CEABA25C683D4503CB11CAF91A39727593CF2BEEAE8032EFACC44FDF8DA31D6007139D4595E8655C7495CF46A9D593A83E3C65B2CBF2AF1EEA02D1F96951A946616B5AB21CA0BF34D12D05F6AE183508A7AC7A46913BDCE5FDC3914CA750018B130CA5BAD49AD8C02291ACA5CFFD"
                exponent = "03"
                checksum = "D7DD7AAC8B67A9A3CC72F35F5D96F265E16EB3FE"
            }
        }

        // China Union Pay 2
        card(this) {
            name = "CUP"
            aid = "A000000333010101"
            partialMatch = true

            key(this) {
                id = "08"
                expiry = "301231"
                modulus = "B61645EDFD5498FB246444037A0FA18C0F101EBD8EFA54573CE6E6A7FBF63ED21D66340852B0211CF5EEF6A1CD989F66AF21A8EB19DBD8DBC3706D135363A0D683D046304F5A836BC1BC632821AFE7A2F75DA3C50AC74C545A754562204137169663CFCC0B06E67E2109EBA41BC67FF20CC8AC80D7B6EE1A95465B3B2657533EA56D92D539E5064360EA4850FED2D1BF"
                exponent = "03"
                checksum = "EE23B616C95C02652AD18860E48787C079E8E85A"
            }

            key(this) {
                id = "12"
                expiry = "301231"
                modulus = "DED9E1BC8E749CAD749484BFB472445BC81FFAA89707648C342AA30D1BE60D5ED0F6CEABA25C683D4503CB11CAF91A39727593CF2BEEAE8032EFACC44FDF8DA31D6007139D4595E8655C7495CF46A9D593A83E3C65B2CBF2AF1EEA02D1F96951A946616B5AB21CA0BF34D12D05F6AE183508A7AC7A46913BDCE5FDC3914CA750018B130CA5BAD49AD8C02291ACA5CFFD"
                exponent = "03"
                checksum = "D7DD7AAC8B67A9A3CC72F35F5D96F265E16EB3FE"
            }
        }

        // China Union Pay 3
        card(this) {
            name = "CUP"
            aid = "A000000333010103"
            partialMatch = true

            key(this) {
                id = "08"
                expiry = "301231"
                modulus = "B61645EDFD5498FB246444037A0FA18C0F101EBD8EFA54573CE6E6A7FBF63ED21D66340852B0211CF5EEF6A1CD989F66AF21A8EB19DBD8DBC3706D135363A0D683D046304F5A836BC1BC632821AFE7A2F75DA3C50AC74C545A754562204137169663CFCC0B06E67E2109EBA41BC67FF20CC8AC80D7B6EE1A95465B3B2657533EA56D92D539E5064360EA4850FED2D1BF"
                exponent = "03"
                checksum = "EE23B616C95C02652AD18860E48787C079E8E85A"
            }

            key(this) {
                id = "12"
                expiry = "301231"
                modulus = "DED9E1BC8E749CAD749484BFB472445BC81FFAA89707648C342AA30D1BE60D5ED0F6CEABA25C683D4503CB11CAF91A39727593CF2BEEAE8032EFACC44FDF8DA31D6007139D4595E8655C7495CF46A9D593A83E3C65B2CBF2AF1EEA02D1F96951A946616B5AB21CA0BF34D12D05F6AE183508A7AC7A46913BDCE5FDC3914CA750018B130CA5BAD49AD8C02291ACA5CFFD"
                exponent = "03"
                checksum = "D7DD7AAC8B67A9A3CC72F35F5D96F265E16EB3FE"
            }
        }

        // Master Card 1
        card(this) {
            name = "Master Card"
            aid = "A0000000041010"
            partialMatch = true

            key(this) {
                id = "05"
                expiry = "241231"
                modulus = "B8048ABC30C90D976336543E3FD7091C8FE4800DF820ED55E7E94813ED00555B573FECA3D84AF6131A651D66CFF4284FB13B635EDD0EE40176D8BF04B7FD1C7BACF9AC7327DFAA8AA72D10DB3B8E70B2DDD811CB4196525EA386ACC33C0D9D4575916469C4E4F53E8E1C912CC618CB22DDE7C3568E90022E6BBA770202E4522A2DD623D180E215BD1D1507FE3DC90CA310D27B3EFCCD8F83DE3052CAD1E48938C68D095AAC91B5F37E28BB49EC7ED597"
                exponent = "03"
                checksum = "EBFA0D5D06D8CE702DA3EAE890701D45E274C845"
            }
            key(this) {
                id = "06"
                expiry = "271231"
                modulus = "CB26FC830B43785B2BCE37C81ED334622F9622F4C89AAE641046B2353433883F307FB7C974162DA72F7A4EC75D9D657336865B8D3023D3D645667625C9A07A6B7A137CF0C64198AE38FC238006FB2603F41F4F3BB9DA1347270F2F5D8C606E420958C5F7D50A71DE30142F70DE468889B5E3A08695B938A50FC980393A9CBCE44AD2D64F630BB33AD3F5F5FD495D31F37818C1D94071342E07F1BEC2194F6035BA5DED3936500EB82DFDA6E8AFB655B1EF3D0D7EBF86B66DD9F29F6B1D324FE8B26CE38AB2013DD13F611E7A594D675C4432350EA244CC34F3873CBA06592987A1D7E852ADC22EF5A2EE28132031E48F74037E3B34AB747F"
                exponent = "03"
                checksum = "F910A1504D5FFB793D94F3B500765E1ABCAD72D9"
            }
        }

        // Master Card 2
        card(this) {
            name = "Master Card"
            aid = "A0000000044010"
            partialMatch = true

            key(this) {
                id = "05"
                expiry = "241231"
                modulus = "B8048ABC30C90D976336543E3FD7091C8FE4800DF820ED55E7E94813ED00555B573FECA3D84AF6131A651D66CFF4284FB13B635EDD0EE40176D8BF04B7FD1C7BACF9AC7327DFAA8AA72D10DB3B8E70B2DDD811CB4196525EA386ACC33C0D9D4575916469C4E4F53E8E1C912CC618CB22DDE7C3568E90022E6BBA770202E4522A2DD623D180E215BD1D1507FE3DC90CA310D27B3EFCCD8F83DE3052CAD1E48938C68D095AAC91B5F37E28BB49EC7ED597"
                exponent = "03"
                checksum = "EBFA0D5D06D8CE702DA3EAE890701D45E274C845"
            }
            key(this) {
                id = "05"
                expiry = "271231"
                modulus = "CB26FC830B43785B2BCE37C81ED334622F9622F4C89AAE641046B2353433883F307FB7C974162DA72F7A4EC75D9D657336865B8D3023D3D645667625C9A07A6B7A137CF0C64198AE38FC238006FB2603F41F4F3BB9DA1347270F2F5D8C606E420958C5F7D50A71DE30142F70DE468889B5E3A08695B938A50FC980393A9CBCE44AD2D64F630BB33AD3F5F5FD495D31F37818C1D94071342E07F1BEC2194F6035BA5DED3936500EB82DFDA6E8AFB655B1EF3D0D7EBF86B66DD9F29F6B1D324FE8B26CE38AB2013DD13F611E7A594D675C4432350EA244CC34F3873CBA06592987A1D7E852ADC22EF5A2EE28132031E48F74037E3B34AB747F"
                exponent = "03"
                checksum = "F910A1504D5FFB793D94F3B500765E1ABCAD72D9"
            }
        }

        // Master Card 3
        card(this) {
            name = "Master Card"
            aid = "A0000000046000"
            partialMatch = true

            key(this) {
                id = "05"
                expiry = "241231"
                modulus = "B8048ABC30C90D976336543E3FD7091C8FE4800DF820ED55E7E94813ED00555B573FECA3D84AF6131A651D66CFF4284FB13B635EDD0EE40176D8BF04B7FD1C7BACF9AC7327DFAA8AA72D10DB3B8E70B2DDD811CB4196525EA386ACC33C0D9D4575916469C4E4F53E8E1C912CC618CB22DDE7C3568E90022E6BBA770202E4522A2DD623D180E215BD1D1507FE3DC90CA310D27B3EFCCD8F83DE3052CAD1E48938C68D095AAC91B5F37E28BB49EC7ED597"
                exponent = "03"
                checksum = "EBFA0D5D06D8CE702DA3EAE890701D45E274C845"
            }
            key(this) {
                id = "05"
                expiry = "271231"
                modulus = "CB26FC830B43785B2BCE37C81ED334622F9622F4C89AAE641046B2353433883F307FB7C974162DA72F7A4EC75D9D657336865B8D3023D3D645667625C9A07A6B7A137CF0C64198AE38FC238006FB2603F41F4F3BB9DA1347270F2F5D8C606E420958C5F7D50A71DE30142F70DE468889B5E3A08695B938A50FC980393A9CBCE44AD2D64F630BB33AD3F5F5FD495D31F37818C1D94071342E07F1BEC2194F6035BA5DED3936500EB82DFDA6E8AFB655B1EF3D0D7EBF86B66DD9F29F6B1D324FE8B26CE38AB2013DD13F611E7A594D675C4432350EA244CC34F3873CBA06592987A1D7E852ADC22EF5A2EE28132031E48F74037E3B34AB747F"
                exponent = "03"
                checksum = "F910A1504D5FFB793D94F3B500765E1ABCAD72D9"
            }
        }

        // Master Card 4
        card(this) {
            name = "Master Card"
            aid = "A0000000043060"
            partialMatch = true

            key(this) {
                id = "05"
                expiry = "241231"
                modulus = "B8048ABC30C90D976336543E3FD7091C8FE4800DF820ED55E7E94813ED00555B573FECA3D84AF6131A651D66CFF4284FB13B635EDD0EE40176D8BF04B7FD1C7BACF9AC7327DFAA8AA72D10DB3B8E70B2DDD811CB4196525EA386ACC33C0D9D4575916469C4E4F53E8E1C912CC618CB22DDE7C3568E90022E6BBA770202E4522A2DD623D180E215BD1D1507FE3DC90CA310D27B3EFCCD8F83DE3052CAD1E48938C68D095AAC91B5F37E28BB49EC7ED597"
                exponent = "03"
                checksum = "EBFA0D5D06D8CE702DA3EAE890701D45E274C845"
            }
            key(this) {
                id = "05"
                expiry = "271231"
                modulus = "CB26FC830B43785B2BCE37C81ED334622F9622F4C89AAE641046B2353433883F307FB7C974162DA72F7A4EC75D9D657336865B8D3023D3D645667625C9A07A6B7A137CF0C64198AE38FC238006FB2603F41F4F3BB9DA1347270F2F5D8C606E420958C5F7D50A71DE30142F70DE468889B5E3A08695B938A50FC980393A9CBCE44AD2D64F630BB33AD3F5F5FD495D31F37818C1D94071342E07F1BEC2194F6035BA5DED3936500EB82DFDA6E8AFB655B1EF3D0D7EBF86B66DD9F29F6B1D324FE8B26CE38AB2013DD13F611E7A594D675C4432350EA244CC34F3873CBA06592987A1D7E852ADC22EF5A2EE28132031E48F74037E3B34AB747F"
                exponent = "03"
                checksum = "F910A1504D5FFB793D94F3B500765E1ABCAD72D9"
            }
        }

        // Master Card 5
        card(this) {
            name = "Master Card"
            aid = "A0000000041030"
            partialMatch = true

            key(this) {
                id = "05"
                expiry = "241231"
                modulus = "B8048ABC30C90D976336543E3FD7091C8FE4800DF820ED55E7E94813ED00555B573FECA3D84AF6131A651D66CFF4284FB13B635EDD0EE40176D8BF04B7FD1C7BACF9AC7327DFAA8AA72D10DB3B8E70B2DDD811CB4196525EA386ACC33C0D9D4575916469C4E4F53E8E1C912CC618CB22DDE7C3568E90022E6BBA770202E4522A2DD623D180E215BD1D1507FE3DC90CA310D27B3EFCCD8F83DE3052CAD1E48938C68D095AAC91B5F37E28BB49EC7ED597"
                exponent = "03"
                checksum = "EBFA0D5D06D8CE702DA3EAE890701D45E274C845"
            }
            key(this) {
                id = "05"
                expiry = "271231"
                modulus = "CB26FC830B43785B2BCE37C81ED334622F9622F4C89AAE641046B2353433883F307FB7C974162DA72F7A4EC75D9D657336865B8D3023D3D645667625C9A07A6B7A137CF0C64198AE38FC238006FB2603F41F4F3BB9DA1347270F2F5D8C606E420958C5F7D50A71DE30142F70DE468889B5E3A08695B938A50FC980393A9CBCE44AD2D64F630BB33AD3F5F5FD495D31F37818C1D94071342E07F1BEC2194F6035BA5DED3936500EB82DFDA6E8AFB655B1EF3D0D7EBF86B66DD9F29F6B1D324FE8B26CE38AB2013DD13F611E7A594D675C4432350EA244CC34F3873CBA06592987A1D7E852ADC22EF5A2EE28132031E48F74037E3B34AB747F"
                exponent = "03"
                checksum = "F910A1504D5FFB793D94F3B500765E1ABCAD72D9"
            }
        }

        // Visa
        card(this) {
            name = "Visa"
            aid = "A0000000031010"
            partialMatch = true

            key(this) {
                id = "01"
                expiry = "151231"
                modulus = "AB9EFC1DF01A2F455EE6AD070DD890F63964CB59D96148FF9FFCDD0BD7B58CA3FC105898C8853E9A82DAB83F3A17F0DB8F7119D58603608EDAA5A7E874582680F9ACEBEF0B6F3B3AA6DFE35B9700C8F830199E5E64FE1F6C4F87E801732B4FF4F2E2686E8C4CA21F45B051999EDD48FF0FACE5B50D281E60838C380973CB3263"
                exponent = "03"
                checksum = "50B7B13F6F8370DEE1D4D35F2D989C04005DC50E"
            }

            key(this) {
                id = "90"
                expiry = "151231"
                modulus = "C26B3CB3833E42D8270DC10C8999B2DA18106838650DA0DBF154EFD51100AD144741B2A87D6881F8630E3348DEA3F78038E9B21A697EB2A6716D32CBF26086F1"
                exponent = "03"
                checksum = "B3AE2BC3CAFC05EEEFAA46A2A47ED51DE679F823"
            }

            key(this) {
                id = "94"
                expiry = "151231"
                modulus = "ACD2B12302EE644F3F835ABD1FC7A6F62CCE48FFEC622AA8EF062BEF6FB8BA8BC68BBF6AB5870EED579BC3973E121303D34841A796D6DCBC41DBF9E52C4609795C0CCF7EE86FA1D5CB041071ED2C51D2202F63F1156C58A92D38BC60BDF424E1776E2BC9648078A03B36FB554375FC53D57C73F5160EA59F3AFC5398EC7B67758D65C9BFF7828B6B82D4BE124A416AB7301914311EA462C19F771F31B3B57336000DFF732D3B83DE07052D730354D297BEC72871DCCF0E193F171ABA27EE464C6A97690943D59BDABB2A27EB71CEEBDAFA1176046478FD62FEC452D5CA393296530AA3F41927ADFE434A2DF2AE3054F8840657A26E0FC617"
                exponent = "03"
                checksum = "C4A3C43CCF87327D136B804160E47D43B60E6E0F"
            }

            key(this) {
                id = "92"
                expiry = "151231"
                modulus = "996AF56F569187D09293C14810450ED8EE3357397B18A2458EFAA92DA3B6DF6514EC060195318FD43BE9B8F0CC669E3F844057CBDDF8BDA191BB64473BC8DC9A730DB8F6B4EDE3924186FFD9B8C7735789C23A36BA0B8AF65372EB57EA5D89E7D14E9C7B6B557460F10885DA16AC923F15AF3758F0F03EBD3C5C2C949CBA306DB44E6A2C076C5F67E281D7EF56785DC4D75945E491F01918800A9E2DC66F60080566CE0DAF8D17EAD46AD8E30A247C9F"
                exponent = "03"
                checksum = "429C954A3859CEF91295F663C963E582ED6EB253"
            }

            key(this) {
                id = "95"
                expiry = "151231"
                modulus = "BE9E1FA5E9A803852999C4AB432DB28600DCD9DAB76DFAAA47355A0FE37B1508AC6BF38860D3C6C2E5B12A3CAAF2A7005A7241EBAA7771112C74CF9A0634652FBCA0E5980C54A64761EA101A114E0F0B5572ADD57D010B7C9C887E104CA4EE1272DA66D997B9A90B5A6D624AB6C57E73C8F919000EB5F684898EF8C3DBEFB330C62660BED88EA78E909AFF05F6DA627B"
                exponent = "03"
                checksum = "EE1511CEC71020A9B90443B37B1D5F6E703030F6"
            }

            key(this) {
                id = "96"
                expiry = "151231"
                modulus = "B74586D19A207BE6627C5B0AAFBC44A2ECF5A2942D3A26CE19C4FFAEEE920521868922E893E7838225A3947A2614796FB2C0628CE8C11E3825A56D3B1BBAEF783A5C6A81F36F8625395126FA983C5216D3166D48ACDE8A431212FF763A7F79D9EDB7FED76B485DE45BEB829A3D4730848A366D3324C3027032FF8D16A1E44D8D"
                exponent = "03"
                checksum = "7616E9AC8BE014AF88CA11A8FB17967B7394030E"
            }

            key(this) {
                id = "97"
                expiry = "151231"
                modulus = "AF0754EAED977043AB6F41D6312AB1E22A6809175BEB28E70D5F99B2DF18CAE73519341BBBD327D0B8BE9D4D0E15F07D36EA3E3A05C892F5B19A3E9D3413B0D97E7AD10A5F5DE8E38860C0AD004B1E06F4040C295ACB457A788551B6127C0B29"
                exponent = "03"
                checksum = "8001CA76C1203955E2C62841CD6F201087E564BF"
            }

            key(this) {
                id = "99"
                expiry = "091231"
                modulus = "AB79FCC9520896967E776E64444E5DCDD6E13611874F3985722520425295EEA4BD0C2781DE7F31CD3D041F565F747306EED62954B17EDABA3A6C5B85A1DE1BEB9A34141AF38FCF8279C9DEA0D5A6710D08DB4124F041945587E20359BAB47B7575AD94262D4B25F264AF33DEDCF28E09615E937DE32EDC03C54445FE7E382777"
                exponent = "03"
                checksum = "4ABFFD6B1C51212D05552E431C5B17007D2F5E6D"
            }

            key(this) {
                id = "07"
                expiry = "151231"
                modulus = "A89F25A56FA6DA258C8CA8B40427D927B4A1EB4D7EA326BBB12F97DED70AE5E4480FC9C5E8A972177110A1CC318D06D2F8F5C4844AC5FA79A4DC470BB11ED635699C17081B90F1B984F12E92C1C529276D8AF8EC7F28492097D8CD5BECEA16FE4088F6CFAB4A1B42328A1B996F9278B0B7E3311CA5EF856C2F888474B83612A82E4E00D0CD4069A6783140433D50725F"
                exponent = "03"
                checksum = "B4BC56CC4E88324932CBC643D6898F6FE593B172"
            }

            key(this) {
                id = "08"
                expiry = "141231"
                modulus = "D9FD6ED75D51D0E30664BD157023EAA1FFA871E4DA65672B863D255E81E137A51DE4F72BCC9E44ACE12127F87E263D3AF9DD9CF35CA4A7B01E907000BA85D24954C2FCA3074825DDD4C0C8F186CB020F683E02F2DEAD3969133F06F7845166ACEB57CA0FC2603445469811D293BFEFBAFAB57631B3DD91E796BF850A25012F1AE38F05AA5C4D6D03B1DC2E568612785938BBC9B3CD3A910C1DA55A5A9218ACE0F7A21287752682F15832A678D6E1ED0B"
                exponent = "03"
                checksum = "20D213126955DE205ADC2FD2822BD22DE21CF9A8"
            }

            key(this) {
                id = "09"
                expiry = "161231"
                modulus = "9D912248DE0A4E39C1A7DDE3F6D2588992C1A4095AFBD1824D1BA74847F2BC4926D2EFD904B4B54954CD189A54C5D1179654F8F9B0D2AB5F0357EB642FEDA95D3912C6576945FAB897E7062CAA44A4AA06B8FE6E3DBA18AF6AE3738E30429EE9BE03427C9D64F695FA8CAB4BFE376853EA34AD1D76BFCAD15908C077FFE6DC5521ECEF5D278A96E26F57359FFAEDA19434B937F1AD999DC5C41EB11935B44C18100E857F431A4A5A6BB65114F174C2D7B59FDF237D6BB1DD0916E644D709DED56481477C75D95CDD68254615F7740EC07F330AC5D67BCD75BF23D28A140826C026DBDE971A37CD3EF9B8DF644AC385010501EFC6509D7A41"
                exponent = "03"
                checksum = "1FF80A40173F52D7D27E0F26A146A1C8CCB29046"
            }
        }

        // Visa Electron
        card(this) {
            name = "Visa Electron"
            aid = "A0000000032010"
            partialMatch = true

            key(this) {
                id = "07"
                expiry = "12312021"
                modulus = "A89F25A56FA6DA258C8CA8B40427D927B4A1EB4D7EA326BBB12F97DED70AE5E4480FC9C5E8A972177110A1CC318D06D2F8F5C4844AC5FA79A4DC470BB11ED635699C17081B90F1B984F12E92C1C529276D8AF8EC7F28492097D8CD5BECEA16FE4088F6CFAB4A1B42328A1B996F9278B0B7E3311CA5EF856C2F888474B83612A82E4E00D0CD4069A6783140433D50725F"
                exponent = "04"
                checksum = "B4BC56CC4E88324932CBC643D6898F6FE593B172"
            }

            key(this) {

                id = "08"
                expiry = "12312021"
                modulus = "D9FD6ED75D51D0E30664BD157023EAA1FFA871E4DA65672B863D255E81E137A51DE4F72BCC9E44ACE12127F87E263D3AF9DD9CF35CA4A7B01E907000BA85D24954C2FCA3074825DDD4C0C8F186CB020F683E02F2DEAD3969133F06F7845166ACEB57CA0FC2603445469811D293BFEFBAFAB57631B3DD91E796BF850A25012F1AE38F05AA5C4D6D03B1DC2E568612785938BBC9B3CD3A910C1DA55A5A9218ACE0F7A21287752682F15832A678D6E1ED0B"
                exponent = "04"
                checksum = "20D213126955DE205ADC2FD2822BD22DE21CF9A8"
            }

            key(this) {
                id = "09"
                expiry = "12312021"
                modulus = "9D912248DE0A4E39C1A7DDE3F6D2588992C1A4095AFBD1824D1BA74847F2BC4926D2EFD904B4B54954CD189A54C5D1179654F8F9B0D2AB5F0357EB642FEDA95D3912C6576945FAB897E7062CAA44A4AA06B8FE6E3DBA18AF6AE3738E30429EE9BE03427C9D64F695FA8CAB4BFE376853EA34AD1D76BFCAD15908C077FFE6DC5521ECEF5D278A96E26F57359FFAEDA19434B937F1AD999DC5C41EB11935B44C18100E857F431A4A5A6BB65114F174C2D7B59FDF237D6BB1DD0916E644D709DED56481477C75D95CDD68254615F7740EC07F330AC5D67BCD75BF23D28A140826C026DBDE971A37CD3EF9B8DF644AC385010501EFC6509D7A41"
                exponent = "04"
                checksum = "1FF80A40173F52D7D27E0F26A146A1C8CCB29046"
            }
        }

        // E-tranzact
        card(this) {


            name = "E-tranzact"
            aid = "A0000004540010"
            partialMatch = true

            key(this) {
                id = "FF"
                expiry = "12312021"
                modulus = "92ED9C031D77D75B8FEF40ADD0D03811A4EF7E9A316DF38159824F06CA5B5E43EFEE25D030A59BAFDF8F9EFB83FE9B934B533D73010BBCCE31E5EA7A0285BC2B95EF309CE9DB6BD72DFA84A04FE981BA8F14074F3F7A614B3A2011E8D018E5441584C6D027BEF6350B11C75B8BDC915D0E34E1BF2428BADB06A47A1E9BB573464F4B3783C38EDB28D6F2F1BE5E2FA5ADAFE10D0C7268974E74910D436C4F4589D275D6021CCC00C8769110704A3CF99B"
                exponent = "03"
                checksum = "B4BC56CC4E88324932CBC643D6898F6FE593B172"
            }
        }

        //  American Express
        card(this) {

            name = "AMEX"
            aid = "A00000002501"
            partialMatch = true

            key(this) {
                id = "0F"
                expiry = "241231"
                modulus = "C8D5AC27A5E1FB89978C7C6479AF993AB3800EB243996FBB2AE26B67B23AC482C4B746005A51AFA7D2D83E894F591A2357B30F85B85627FF15DA12290F70F05766552BA11AD34B7109FA49DE29DCB0109670875A17EA95549E92347B948AA1F045756DE56B707E3863E59A6CBE99C1272EF65FB66CBB4CFF070F36029DD76218B21242645B51CA752AF37E70BE1A84FF31079DC0048E928883EC4FADD497A719385C2BBBEBC5A66AA5E5655D18034EC5"
                exponent = "03"
                checksum = "A73472B3AB557493A9BC2179CC8014053B12BAB4"
            }

            key(this) {
                id = "10"
                expiry = "271231"
                modulus = "CF98DFEDB3D3727965EE7797723355E0751C81D2D3DF4D18EBAB9FB9D49F38C8C4A826B99DC9DEA3F01043D4BF22AC3550E2962A59639B1332156422F788B9C16D40135EFD1BA94147750575E636B6EBC618734C91C1D1BF3EDC2A46A43901668E0FFC136774080E888044F6A1E65DC9AAA8928DACBEB0DB55EA3514686C6A732CEF55EE27CF877F110652694A0E3484C855D882AE191674E25C296205BBB599455176FDD7BBC549F27BA5FE35336F7E29E68D783973199436633C67EE5A680F05160ED12D1665EC83D1997F10FD05BBDBF9433E8F797AEE3E9F02A34228ACE927ABE62B8B9281AD08D3DF5C7379685045D7BA5FCDE58637"
                exponent = "03"
                checksum = "C729CF2FD262394ABC4CC173506502446AA9B9FD"
            }
        }
    }
}

internal fun getDefaultTerminalConfig(): TerminalConfig {
    return TerminalConfig(
            supportpse = true,
            floorlimitcheck = true,
            floorlimit = 100,
            tacdenial = "0010000000",
            taconline = "FCF8E4F880",
            tacdefault = "FCF0E40800",
            ddol = "9F3704",
            tdol = "N/A",
            version = "0020",
            riskdata = "",
            terminalcountrycode = "0566",
            terminaltype = "34",
            terminalcapability = "E0F8C8",
            extendedterminalcapability = "E000F0A001",
            referercurrencycode = "0566",
            merchantcatcode = ""
    )
}