const fieldSize = 10;
let lastPlayerId = null;
let playerId = null;
const fieldId = new URLSearchParams(window.location.search).get('fieldId');
const sessionId = getSessionId();
let gameFinished = false;

function connect() {
    if (sessionId) {
        const socket = new SockJS('/tic-tac-toe', {}, {
            sessionId: function () {
                return sessionId;
            }
        });

        stompClient = Stomp.over(socket);
        stompClient.connect({}, function () {
            stompClient.send(`/app/fields/${fieldId}/join`);
            stompClient.subscribe(`/app/players/${sessionId}`, setPlayer);
            stompClient.subscribe(`/app/fields/${fieldId}`, setField);
            stompClient.subscribe(`/topic/field/${fieldId}/move`, addMove);
        });
    }
}

function setPlayer(message) {
    player = getMessagePayload(message);
    if (player) {
        playerId = player.id;
    } else {
        showErrorModal("You should visit main page to create account before you could join to the game.");
    }
}

function setField(message) {
    let field = getMessagePayload(message);
    if (field) {
        lastPlayerId = field.lastPlayerId;
        field.cells.forEach(function (row, rowId) {
            row.forEach(function (side, colId) {
                if (side) {
                    fillCell(rowId * fieldSize + colId, side);
                }
            });
        })
    } else {
        showErrorModal("Field not found.");
    }
}

function addMove(message) {
    let move = getMessagePayload(message);
    if (move) {
        fillCell(move.cellId, move.side);
        lastPlayerId = move.player.id;
        if (move.result == 'WIN') {
            gameFinished = true;
            showGameFinishedModal(`Winner is ${move.player.name}!`)
        } else if (move.result == 'DRAW') {
            gameFinished = true;
            showGameFinishedModal("Draw")
        }
    }
}

function makeMove(event) {
    if (lastPlayerId == playerId) {
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

function getSessionId() {
    let savedSessionId = sessionStorage.getItem("sessionId");
    if (savedSessionId) {
        return savedSessionId;
    }
    showErrorModal("You should visit main page to create account before you could join to the game.");
    return null;
}

$(window).bind(
    "beforeunload",
    function () {
        if (!gameFinished) {
            stompClient.send(`/app/fields/${fieldId}/leave`, {}, {});
        }
    }
);

$(document).ready(function () {
    chooseRandomSide();
    createField();
    connect();
});