const fieldSize = 10;
let lastMoveUserId = null;
let userId = null;

{
    let searchParams = new URLSearchParams(window.location.search)
    const fieldId = searchParams.get('fieldId');
    const sessionId = searchParams.get('sessionId');
}

$.getScript("field_ui.js");

$(document).ready(function () {
    chooseRandomSide();
    createField();
    connect();
});

function connect() {
    const socket = new SockJS('/tic-tac-toe', {}, {
        sessionId: function () {
            return sessionId;
        }
    });

    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        stompClient.subscribe(`/app/users/${sessionId}`, setUser);
        stompClient.subscribe(`/app/fields/${fieldId}`, setField);
        stompClient.subscribe(`/topic/field/${fieldId}/move`, addMove);
    });
}

function setUser(userJson) {
    user = JSON.parse(userJson.body);
    userId = user.id;
}

function setField(fieldJson) {
    let field = JSON.parse(fieldJson.body);
    field.cells.forEach(function (side, cellId) {
        if (side) {
            fillCell(cellId, side)
        }
    })
}

function addMove(moveJson) {
    let move = JSON.parse(moveJson.body);
    fillCell(move.cellId, move.side);
    lastMoveUserId = move.user.id;
    if (move.result == 'WIN') {
        showModal(`Winner is ${move.user.name}!`)
    } else if (move.result == 'DRAW') {
        showModal("Draw")
    }
}

function makeMove(event) {
    if (lastMoveUserId == userId) {
        showAlert("Sorry! You can't make multiple moves in a row");
        return;
    }
    let cellId = event.data.cellId;
    if (isCellFree(cellId)) {
        stompClient.send(`/app/fields/${fieldId}/move`, {}, JSON.stringify({
            'cellId': cellId,
            'side': $("#select-side").val()
        }));
    }
}