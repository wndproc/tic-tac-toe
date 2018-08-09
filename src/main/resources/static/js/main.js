let stompClient = null;

function connect() {
    const socket = new SockJS('/tic-tac-toe');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        login();
        stompClient.subscribe('/app/fields', function (fields) {
            JSON.parse(fields.body).forEach(function (field) {
                addOrUpdateField(field)
            })
        });
        stompClient.subscribe('/topic/field', function (field) {
            addOrUpdateField(JSON.parse(field.body));
        });
    });
}

function login() {
    let name = "";
    while (name == "") name = prompt("Enter your name");
    stompClient.send("/app/login", {}, JSON.stringify({'name': name}));
}

function addOrUpdateField(field) {
    if ($(`#field_${field.id}`).length) {
        $(`#field_${field.id}_players`).html(field.players.length);
    } else {
        $("#fields").append(
            `<tr id="field_${field.id}" fieldId="${field.id}">` +
            `<td>${field.name}</td>` +
            `<td id="field_${field.id}_players">${field.players.length}</td>` +
            `</tr>`
        );
        $(`#field_${field.id}`).click(function () {
            let fieldId = $(this).attr("fieldId");
            joinField(fieldId)
        });
    }
}

function createField() {
    stompClient.send("/app/field/create", {}, JSON.stringify({'name': $("#fieldName").val()}));
}

function joinField(fieldId) {
    stompClient.send("/app/field/join", {}, JSON.stringify({'id': fieldId}));
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#createField").click(function () {
        createField();
    });
});

connect();
