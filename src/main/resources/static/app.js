var stompClient = null;

function connect() {
    var socket = new SockJS('/tic-tac-toe');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        login();
        stompClient.subscribe('/app/fields', function (fields) {
            JSON.parse(fields.body).forEach(function (field) {
                showField(field)
            })
        });
        stompClient.subscribe('/topic/field', function (field) {
            showField(JSON.parse(field.body));
        });
    });
}

function createField() {
    stompClient.send("/app/field/create", {}, JSON.stringify({'name': $("#fieldName").val()}));
}

function showField(field) {
    $("#fields").append("<tr><td>" + field.name + "</td></tr>");
}

function login() {
    var name = "";
    while (name == "") name = prompt("Enter your name");
    stompClient.send("/app/login", {}, JSON.stringify({'name': name}));
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

