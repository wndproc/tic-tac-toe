let stompClient = null;
const sessionId = uuidv4();
let user;

function connect() {
    const socket = new SockJS('/tic-tac-toe', {}, {
        sessionId: function () {
            return sessionId;
        }
    });

    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        login();
    });
}

function login() {
    let name = "";
    while (name == "") name = prompt("Enter your name");
    stompClient.subscribe(`/user/queue/user`, function (userJson) {
        user = JSON.parse(userJson.body);
        stompClient.subscribe('/app/fields', function (fields) {
            JSON.parse(fields.body).forEach(function (field) {
                addOrUpdateField(field)
            })
        });
        stompClient.subscribe('/topic/fields', function (field) {
            addOrUpdateField(JSON.parse(field.body));
        });
    });
    stompClient.send("/app/users/create", {}, JSON.stringify({'name': name}));
}

function addOrUpdateField(field) {
    if (field.ownerId == user.id) {
        window.location.href = `/field.html?fieldId=${field.id}&sessionId=${sessionId}`;
    }
    if ($(`#field_${field.id}`).length) {
        if (field.playersNumber) {
            $(`#field_${field.id}_players`).html(field.playersNumber);
        }
        if (field.lastMoveTime) {
            $(`#field_${field.id}_last_move_time`).html(field.lastMoveTime);
        }
    } else {
        $("#fields").append(
            `<tr id="field_${field.id}">` +
            `<td>${field.name}</td>` +
            `<td id="field_${field.id}_players">${field.playersNumber}</td>` +
            `<td id="field_${field.id}_last_move_time">${field.lastMoveTime ? field.lastMoveTime : ""}</td>` +
            `<td><button id="field_${field.id}_join" fieldId="${field.id}">Join</button></td>` +
            `</tr>`
        );
        $(`#field_${field.id}_join`).click(function () {
            let fieldId = $(this).attr("fieldId");
            joinField(fieldId);
            window.location.href = `/field.html?fieldId=${fieldId}&sessionId=${sessionId}`;
        });
    }
}

function createField() {
    stompClient.send("/app/fields/create", {}, JSON.stringify({'name': $("#fieldName").val()}));
}

function joinField(fieldId) {
    stompClient.send(`/app/fields/${fieldId}/join`);
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#createField").click(function () {
        createField();
    });
});

function uuidv4() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        let r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

connect();

