// https://github.com/EntenKoeniq
const makeCollection = collection => {
    collection.disabled = (option) => {
        'boolean' === typeof option ? collection.forEach((e) => {
            e.disabled = option;
        }) : console.log('Oops! this is not a boolean value!');
    }

    collection.click = () => {
        collection.forEach((e) => {
            e.click();
        });
    }

    collection.toggleIt = (data) => {
        collection.forEach((e) => {
            e.classList.toggle(data);
        });
    }

    collection.html = (data) => {
        collection.forEach((e) => {
            e.innerHTML = data;
        });
    }

    collection.append = (data) => {
        collection.forEach((e) => {
            e.appendChild(data);
        });
    };

    collection.remove = () => {
        collection.forEach((e) => {
            e.remove();
        });
    };

    collection.each = (callback) => {
        collection.forEach((e, i) => {
            const boundFn = callback.bind(e);
            boundFn(i, e);
        });
    };

    collection.on = (eventName, handler) => {
        collection.forEach((e) => {
            e.addEventListener(eventName, handler);
        });
    };

    collection.css = (...cssArgs) => {
        if (typeof cssArgs[0] === 'string') {
            const [property, value] = cssArgs;
            collection.forEach((e) => {
                e.style[property] = value;
            });
        } else if (typeof cssArgs[0] === 'object') {
            const cssProps = Object.entries(cssArgs[0]);
            collection.forEach((e) => {
                cssProps.forEach(([property, value]) => {
                    e.style[property] = value;
                });
            });
        }
    };
};
  
const $ = (...args) => {
    if (typeof args[0] === 'function') {
        // document ready listener
        document.addEventListener('DOMContentLoaded', args[0]);
    } else if (typeof args[0] === 'string') {
        // select an element!
        const collection = document.querySelectorAll(args[0]);
        if (collection.length === 0) return null;
        makeCollection(collection);
        return collection;
    } else if (args[0] instanceof HTMLElement) {
        // we have an element!
        const collection = [args[0]];
        makeCollection(collection);
        return collection;
    }
};