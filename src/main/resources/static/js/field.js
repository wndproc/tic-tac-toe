const fieldSize = 10;
let searchParams = new URLSearchParams(window.location.search);
const fieldId = searchParams.get('fieldId');
const sessionId = searchParams.get('sessionId');
let lastMoveUser = null;

$(document).ready(function () {
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
            lastMoveUser = move.user;
            if (move.result == 'WIN') {
                alert(`Winner is ${move.user.name}!`)
            } else if (move.result == 'DRAW') {
                alert("Draw")
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
            $(`#${rowId}`).append(`<td id="${tdId}" class="cell free"></td>`);
            $(`#${tdId}`).click({cellId: cellId}, makeMove);
        }
        $("#field").append("</tr>");
    }
}

function isCellFree(cellId) {
    return !$(`#cell_${cellId}`).html();
}

function makeMove(event) {
    let cellId = event.data.cellId;
    if (isCellFree(cellId)) {
        stompClient.send(`/app/fields/${fieldId}/move`, {}, JSON.stringify({
            'cellId': cellId,
            'cellType': $("#select").val()
        }));
    }
}

function fillCell(cellId, type) {
    if (isCellFree(cellId)) {
        $(`#cell_${cellId}`).html(type);
        $(`#cell_${cellId}`).removeClass("free")
    }
}

function getSelectedType() {
    return $("#select").val()
}