function chooseRandomSide() {
    if (Math.random() >= 0.5) {
        $(`#select-x`).prop('selected', true);
    } else {
        $(`#select-o`).prop('selected', true);
    }
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

function fillCell(cellId, side) {
    if (isCellFree(cellId)) {
        $(`#field-cell-${cellId}`).html(side);
        $(`#field-cell-${cellId}`).removeClass("cell-free")
    }
}

function isCellFree(cellId) {
    return !$(`#field-cell-${cellId}`).html();
}

function showGameFinishedModal(text) {
    $(`#modal-gf-label`).html(text);
    $('#modal-game-finished').modal({
        backdrop: 'static',
        keyboard: false
    });
}

function showErrorModal(text) {
    $(`#modal-error .modal-body`).html(text);
    $('#modal-error').modal({
        backdrop: 'static',
        keyboard: false
    });
}

function showAlert(text) {
    $('#alert-placeholder').html(`
    <div id="alert-custom" class="alert alert-warning fade show" data-alert="alert">
        <div>${text}</div>
    </div>
    `);
    $("#div-select-side").hide();
    $(".alert").delay(1500).fadeOut("slow", function () {
        $(this).remove();
        $("#div-select-side").show();
    });
}