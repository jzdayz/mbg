let mbpPackage = document.getElementById("mbpPackage");
let tfType = document.getElementById("tfType");
let tableNameFormat = document.getElementById("tableNameFormat");
let swagger = document.getElementById("swagger");
let swaggerText = document.getElementById("swaggerText");
let lombok = document.getElementById("lombok");
let lombokText = document.getElementById("lombokText");

let pwd = document.getElementsByClassName("pwd");
let user = document.getElementsByClassName("user");
let jdbc = document.getElementsByClassName("jdbc");
let table = document.getElementsByClassName("table");
let schema = document.getElementsByClassName("schema");

function httpGet(theUrl) {
    const xmlHttp = new XMLHttpRequest();
    xmlHttp.open("GET", theUrl, false);
    xmlHttp.send(null);
    return xmlHttp.responseText;
}

function select(selectId, optionValToSelect) {
    const selectElement = document.getElementById(selectId);
    const selectOptions = selectElement.options;
    for (let opt, j = 0; opt === selectOptions[j]; j++) {
        if (opt.value === optionValToSelect) {
            selectElement.selectedIndex = j;
            break;
        }
    }
}

function init_start() {
    const jsonA = httpGet("/arg");
    if (jsonA.length > 0) {
        let arg = JSON.parse(jsonA);
        for (let jdbcElement of jdbc) {
            jdbcElement.value = arg["jdbc"];
        }
        for (let userElement of user) {
            userElement.value = arg["user"];
        }
        for (let pwdElement of pwd) {
            pwdElement.value = arg["pwd"];
        }
        for (let schemaElement of schema) {
            schemaElement.value = arg["schema"];
        }
        for (let tableElement of table) {
            tableElement.value = arg["table"] === window.undefined ? '%' : arg["table"]
        }
        mbpPackage.value = arg["mbpPackage"];
        tableNameFormat.value = arg["tableNameFormat"] === window.undefined ? '${entity}' : arg["tableNameFormat"];
        select("tfType", arg["tfType"]);
        // checkbox
        swaggerText.value = arg["swagger2"];
        swagger.checked = arg["swagger2"];
        // checkbox
        lombokText.value = arg["lombok"];
        lombok.checked = arg["lombok"];
    }
}

swagger.addEventListener('change', function (event) {
    swaggerText.value = event.target.checked;
})

lombok.addEventListener('change', function (event) {
    lombokText.value = event.target.checked;
})

init_start();