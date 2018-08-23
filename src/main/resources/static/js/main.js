let stompClient = null;
let isNewSession = false;
const sessionId = getSessionId();
let player;

function getSessionId() {
    let savedSessionId = sessionStorage.getItem("sessionId");
    if (savedSessionId) {
        return savedSessionId;
    }
    let uuid = uuidv4();
    sessionStorage.setItem("sessionId", uuid);
    isNewSession = true;
    return uuid;
}

function connect() {
    const socket = new SockJS('/tic-tac-toe', {}, {
        sessionId: function () {
            return sessionId;
        }
    });

    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnect);
}

function onConnect() {
    if (isNewSession) {
        showCreatePlayerModal();
    } else {
        stompClient.subscribe(`/app/players/${sessionId}`, function (message) {
            player = getMessagePayload(message);
            if (!player) {
                showCreatePlayerModal();
            }
        });
    }

    stompClient.subscribe('/app/fields', function (message) {
        let fields = getMessagePayload(message);
        if (fields) {
            fields.forEach(function (field) {
                addOrUpdateField(field)
            })
        }
    });

    stompClient.subscribe('/topic/fields', function (message) {
        let field = getMessagePayload(message);
        if (field) {
            if (player && player.id == field.creatorId) {
                window.location.href = `field.html?fieldId=${field.id}`;
            } else {
                addOrUpdateField(field);
            }
        }
    });

    stompClient.subscribe('/topic/fields/delete', function (message) {
        let field = getMessagePayload(message);
        if (field) {
            deleteField(field.id)
        }
    });
}

function createPlayer() {
    hideCreatePlayerModal();
    stompClient.subscribe(`/user/queue/player`, function (message) {
        player = getMessagePayload(message);
        if (!player) {
            showErrorModal();
        }
    });
    stompClient.send("/app/players/create", {}, JSON.stringify({'name': getPlayerName()}));
}

function createField() {
    stompClient.send("/app/fields/create", {}, JSON.stringify({'name': getFieldName()}));
}

$(document).ready(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    connect();
});