<#-- @ftlvariable name="addressBook" type="billiongoods.server.services.address.AddressBook" -->

<div class="addresses">
    <table>
        <thead>
        <tr>
            <th>Получатель</th>
            <th>Индекс</th>
            <th>Регион</th>
            <th>Город</th>
            <th>Адрес</th>
            <th></th>
        </tr>
        </thead>

        <tbody>
        <#list addressBook.addresses as a>
        <tr class="address" id="addressRecord${a.id}">
            <td><span class="firstName">${a.firstName}</span> <span class="lastName">${a.lastName}</span></td>
            <td><span class="postcode">${a.postcode}</span></td>
            <td><span class="region">${a.region}</span></td>
            <td><span class="city">${a.city}</span></td>
            <td><span class="location">${a.location}</span></td>

            <td align="right" nowrap="nowrap">
                <button type="button" onclick="addressBook.editAddress(${a.id});">Изменить</button>
                <button type="button" onclick="addressBook.removeAddress(${a.id});">Удалить</button>
            </td>
        </tr>
        </#list>
        </tbody>
    </table>

    <form id="addressForm" action="" style="display: none">
        <input type="hidden" id="id" name="id">
        <table>
            <tr>
                <td>
                    <label for="firstName">Ваше имя и фамилия:</label>
                </td>
                <td style="padding-bottom: 20px">
                    <div>
                        <input id="firstName" name="firstName" style="width: 38%;">
                        <input id="lastName" name="lastName" style="width: 60%; float: right">
                    </div>

                    <div class="sample">Например: Ivan Ivanov</div>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="postcode">Индекс:</label>
                </td>
                <td>
                    <input id="postcode" name="postcode">

                    <div class="sample">Например: 123321</div>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="region">Область/Регион:</label>
                </td>
                <td>
                    <input id="region" name="region">

                    <div class="sample">Например: Leningradskaya oblast, Gatchinskii raion</div>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="city">Населенный пункт:</label>
                </td>
                <td>
                    <input id="city" name="city">

                    <div class="sample">Например: Gadchinskoye</div>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="location">Адрес:</label>
                </td>
                <td>
                    <input id="location" name="location">

                    <div class="sample">Например: ul. Tretiya sleva, d. 321/98, korp. 7, kv. 654</div>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <i>* все поля заполняются латинскими буквами</i>
                </td>
            </tr>
        </table>
    </form>
</div>

<div style="padding-top: 20px">
    <button class="bg-ui-button" type="button" onclick="addressBook.registerNew()">Добавить новый адрес</button>
</div>

<script type="text/javascript">
    var addressBook = new bg.privacy.AddressBook();
</script>
