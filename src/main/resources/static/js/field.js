const fieldSize = 10;
let searchParams = new URLSearchParams(window.location.search);
const fieldId = searchParams.get('fieldId');
const sessionId = searchParams.get('sessionId');
let lastMoveUserId = null;
let userId = null;

$(document).ready(function () {
    if (Math.random() >= 0.5) {
        $(`#select-x`).prop('selected', true);
    } else {
        $(`#select-o`).prop('selected', true);
    }
    createField();
    fillCell(0, 'X');
    fillCell(1, 'O');
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
            field.cells.forEach(function (side, cellId) {
                if (side) {
                    fillCell(cellId, side)
                }
            })
        });
        stompClient.subscribe(`/topic/field/${fieldId}/move`, function (moveJson) {
            let move = JSON.parse(moveJson.body);
            fillCell(move.cellId, move.side);
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
        $("#field").append(`<tr id="field-row-${row}">`);
        for (let col = 0; col < fieldSize; col++) {
            let cellId = row * fieldSize + col;
            $(`#field-row-${row}`).append(`<td id="field-cell-${cellId}" class="cell cell-free cell-text"></td>`);
            $(`#field-cell-${cellId}`).click({cellId: cellId}, makeMove);
        }
        $("#field").append("</tr>");
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

function fillCell(cellId, side) {
    if (isCellFree(cellId)) {
        $(`#field-cell-${cellId}`).html(side);
        $(`#field-cell-${cellId}`).removeClass("cell-free")
    }
}

function isCellFree(cellId) {
    return !$(`#field-cell-${cellId}`).html();
}

function showModal(text) {
    $(`#modal-label`).html(text);
    $('#modal').modal({
        backdrop: 'static',
        keyboard: false
    });
}

function showAlert(text) {
    $('#alert-placeholder').html(`
    <div id="alert-custom" class="alert alert-primary fade show" data-alert="alert">
        <div>${text}</div>
    </div>
    `);
    $("#div-select-side").hide();
    $(".alert").delay(1500).fadeOut("slow", function () {
        $(this).remove();
        $("#div-select-side").show();
    });
}