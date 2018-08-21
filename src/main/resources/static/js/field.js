const fieldSize = 10;
let searchParams = new URLSearchParams(window.location.search);
const fieldId = searchParams.get('fieldId');
const sessionId = searchParams.get('sessionId');
let lastMoveUserId = null;
let userId = null;

$(document).ready(function () {
    if(Math.random() >= 0.5) {
        $(`#X`).prop('checked', true);
    } else {
        $(`#O`).prop('checked', true);
    }
    createField();
    fillCell(0, 'X');
    fillCell(1, 'O');
    showModal("Draw");
    //connect();
});

function connect() {
    const socket = new SockJS('/tic-tac-toe', {}, {
        sessionId: function () {
            return sessionId;
        }
    });

    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        stompClient.subscribe(`/app/users/${sessionId}`, function (userJson) {
            user = JSON.parse(userJson.body);
            userId = user.id;
        });
        stompClient.subscribe(`/app/fields/${fieldId}`, function (fieldJson) {
            let field = JSON.parse(fieldJson.body);
            field.cells.forEach(function (cell, cellId) {
                if (cell) {
                    fillCell(cellId, cell)
                }
            })
        });
        stompClient.subscribe(`/topic/field/${fieldId}/move`, function (moveJson) {
            let move = JSON.parse(moveJson.body);
            fillCell(move.cellId, move.cellType);
            lastMoveUserId = move.user.id;
            if (move.result == 'WIN') {
                showModal(`Winner is ${move.user.name}!`)
            } else if (move.result == 'DRAW') {
                showModal("Draw")
            }
        });
    });
}

function createField() {
    for (let row = 0; row < fieldSize; row++) {
        let rowId = `row_${row}`;
        $("#field").append(`<tr id='${rowId}'>`);
        for (let col = 0; col < fieldSize; col++) {
            let cellId = row * fieldSize + col;
            let tdId = `cell_${cellId}`;
            $(`#${rowId}`).append(`<td id="${tdId}" class="cell free cell-text"></td>`);
            $(`#${tdId}`).click({cellId: cellId}, makeMove);
        }
        $("#field").append("</tr>");
    }
}

function isCellFree(cellId) {
    return !$(`#cell_${cellId}`).html();
}

function makeMove(event) {
    if (lastMoveUserId == userId) {
        alert("You can't make two moves in a row");
        return;
    }
    let cellId = event.data.cellId;
    if (isCellFree(cellId)) {
        stompClient.send(`/app/fields/${fieldId}/move`, {}, JSON.stringify({
            'cellId': cellId,
            'cellType': $('#cellType input:radio:checked').val()
        }));
    }
}

function fillCell(cellId, type) {
    if (isCellFree(cellId)) {
        $(`#cell_${cellId}`).html(type);
        $(`#cell_${cellId}`).removeClass("free")
    }
}

function showModal(text) {
    $(`#modalLabel`).html(text);
    $('#modal').modal({
        backdrop: 'static',
        keyboard: false
    });
}