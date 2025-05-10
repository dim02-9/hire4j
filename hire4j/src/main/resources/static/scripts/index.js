function requestAnimationAfter(callback, timeoutMilliseconds) {
    let initialTimestamp;

    requestAnimationFrame(function checkAnimationLoop(timestamp) {
        initialTimestamp ??= timestamp;
        const elapsed = timestamp - initialTimestamp;

        if (elapsed >= timeoutMilliseconds) {
            return callback();
        }

        requestAnimationFrame(checkAnimationLoop);
    });
}

const CLICK_ACTION_MAP = Object.freeze({
    "toggle-dropdown": {
        accepts: [],
        trigger: ({ $event }) => {
            const toggle = $event.currentTarget;
            const shouldExpand = toggle.ariaExpanded === "false";

            toggle.ariaExpanded = shouldExpand;

            if (shouldExpand) {
                function closeDropdown() {
                    toggle.ariaExpanded = false;
                }

                setTimeout(() => {
                    window.addEventListener("click", closeDropdown, { once: true });
                }, 0);
            }
        }
    },
    "open-dialog": {
        accepts: ["targetSelector"],
        trigger: ({ targetSelector }) => {
            const targetElement = document.querySelector(targetSelector);

            if (targetElement === null) {
                return;
            }

            const closeDialogButtons = targetElement.querySelectorAll("[data-action=\"close-dialog\"]");

            function closeDialog({ target, currentTarget }) {
                const isOutsideClick = currentTarget === targetElement && target === targetElement;
                const isButtonClick = currentTarget !== targetElement;

                if (!isOutsideClick && !isButtonClick) {
                    return;
                }

                targetElement.classList.remove("is-open");

                targetElement.removeEventListener("click", closeDialog);
                closeDialogButtons.forEach((closeDialogButton) => closeDialogButton.removeEventListener("click", closeDialog));
            }

            targetElement.addEventListener("click", closeDialog);
            closeDialogButtons.forEach((closeDialogButton) => {
                closeDialogButton.addEventListener("click", closeDialog);
            });

            targetElement.classList.add("is-open");
       }
    },
    "open-drawer": {
        accepts: ["targetSelector"],
        trigger: ({ targetSelector }) => {
            const targetElement = document.querySelector(targetSelector);

            if (targetElement === null) {
                return;
            }

            document.body.overflow = "hidden";

            targetElement.classList.add("is-visible");

            requestAnimationAfter(() => {
                targetElement.classList.add("is-open");
            }, 0);
        }
    },
    "close-drawer": {
        accepts: [],
        trigger: ({ $event }) => {
            const targetElement = $event.currentTarget;

            const isOutsideClick = targetElement === $event.target;

            if (!isOutsideClick) {
                return;
            }

            targetElement.classList.remove("is-open");

            requestAnimationAfter(() => {
                targetElement.classList.remove("is-visible");

                document.body.overflow = null;
            }, 200);
        }
    },
});

function extractActionValues(element, keys) {
    const actionValues = {};

    for (const key of keys) {
        const actionValue = element.dataset[key];

        if (typeof actionValue !== "string") {
            return null;
        }

        actionValues[key] = actionValue;
    }

    return actionValues;
}

function registerClickHandlers() {
    Object
        .entries(CLICK_ACTION_MAP)
        .forEach(([key, action]) => {
            document
                .querySelectorAll(`[data-click-action="${key}"]`)
                .forEach((element) => {
                    const actionValues = extractActionValues(element, action.accepts);

                    if (actionValues === null) {
                        return;
                    }

                    function handleEvent($event) {
                        actionValues.$event = $event;

                        action.trigger(actionValues);
                    }

                    element.addEventListener("click", handleEvent);
                });
        });
}

function registerPillListHandlers() {
    document.querySelectorAll(`input[data-pill-list-target-selector]`)
        .forEach((input) => {
            const targetSelector = input.dataset.pillListTargetSelector;
            const pillInputName = input.dataset.pillListInputName;
            const targetElement = document.querySelector(targetSelector);

            if (targetElement === null) {
                return;
            }

            function handleInput() {
                if (!/\s$/m.test(input.value)) {
                    return;
                }

                const value = input.value.trim();

                const createdPillsArray = Array.from(targetElement.querySelectorAll(".pill > input"))
                    .map((pill) => pill.value.trim());
                const createdPillsSet = new Set(createdPillsArray);

                if (value === "" || createdPillsSet.has(value)) {
                    input.value = input.value.trim();
                    return;
                }

                const pill = document.createElement("div");
                pill.textContent = value;
                pill.classList.add("pill");

                const hiddenInput = document.createElement("input");
                hiddenInput.type = "hidden";
                hiddenInput.name = pillInputName;
                hiddenInput.value = value;

                pill.appendChild(hiddenInput);

                const removeIcon = document.createElement("i");
                removeIcon.classList.add("bi", "bi-x");

                const removeButton = document.createElement("button");
                removeButton.type = "button";
                removeButton.classList.add("pill-remove-button");

                removeButton.appendChild(removeIcon);
                pill.appendChild(removeButton);

                targetElement.appendChild(pill);

                input.value = "";
            }

            input.addEventListener("input", handleInput);
        });

    window.addEventListener("click", (event) => {
        const { target } = event;

        if (!target.classList.contains("pill-remove-button")) {
            return;
        }

        target.parentElement.remove();
    });
}

function main() {
    registerClickHandlers();
    registerPillListHandlers();
}

document.addEventListener("DOMContentLoaded", main, { once: true });