function addOrUpdateField(field) {
    if ($(`#table-fields-row-${field.id}`).length) {
        if (field.playersNumber) {
            $(`#table-fields-row-${field.id} .players`).html(field.playersNumber);
        }
        if (field.lastMoveTime) {
            $(`#table-fields-row-${field.id} .last-move-time`).html(formatDateTime(field.lastMoveTime));
        }
    } else {
        $("#table-fields-body").append(
            `<tr id="table-fields-row-${field.id}" fieldId="${field.id}" class="table-fields-row">` +
            `<td>${field.name}</td>` +
            `<td class="players">${field.playersNumber}</td>` +
            `<td class="last-move-time">${field.lastMoveTime ? formatDateTime(field.lastMoveTime) : ""}</td>` +
            `</tr>`
        );
        $(`#table-fields-row-${field.id}`).click(function () {
            let fieldId = $(this).attr("fieldId");
            joinField(fieldId);
            window.location.href = `field.html?fieldId=${fieldId}`;
        });
    }
}

function deleteField(fieldId) {
    $(`#table-fields-row-${fieldId}`).remove();
}

function showCreatePlayerModal() {
    $('#modal-create-player').modal({
        backdrop: 'static',
        keyboard: false
    });
}

function hideCreatePlayerModal() {
    $('#modal-create-player').modal('toggle');
}

function showErrorModal() {
    $('#modal-error').modal({
        backdrop: 'static',
        keyboard: false
    });
}

function getPlayerName() {
    return $("#player-name").val();
}

function getFieldName() {
    return $("#input-field-name").val();
}

function formatDateTime(dateTime) {
    return dateTime.replace('T', ' ').split('.')[0]
}