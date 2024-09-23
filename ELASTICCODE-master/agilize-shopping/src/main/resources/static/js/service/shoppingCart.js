//----------------------------------------------------------------
// shopping cart
//
function shoppingCart(cartName, $store, $rootScope) {
    this.cartName = cartName;
    this.clearCart = false;
    //this.checkoutParameters = {};
    this.store = $store;
    this.rootScope = $rootScope;
    this.items = [];
    //this.store.set('product_itens', []);
    this.saveItems();

    // load items from local storage when initializing
    //this.loadItems();

    // save items to local storage when unloading
    var self = this;
    $(window).unload(function () {
        if (self.clearCart) {
            self.clearItems();
        }
        self.saveItems();
        self.clearCart = false;
    });
}

//clear the cart
shoppingCart.prototype.clearItems = function () {
    this.items = [];
    this.saveItems();
}

shoppingCart.prototype.loadItens = function (itens) {
    if(isNullValue(itens)) {
    	this.items = isNullValue(this.getItems()) ? [] : this.getItems();
        this.saveItems();
    }
    else {
    	this.items = itens;
        this.saveItems();
    }
	return null;
}

/*shoppingCart.prototype.loadItens = function (itens) {
    if(isNullValue(itens)) {
    	this.items = isNullValue(getItems()) ? [] : getItems();
        this.saveItems();
    }
    else {
    	this.items = itens;
        this.saveItems();
    }
	return null;
}*/


//get the total price for all items currently in the cart
shoppingCart.prototype.getTotalCount = function (id) {
    var count = 0;
    for (var i = 0; i < this.items.length; i++) {
        var item = this.items[i];
        if ((id == null || item._id == id) && item.selected === true) {
            count += this.toNumber(item.amountSelecteds);
        }
    }
    return count;
}

//get the total price for all items currently in the cart
shoppingCart.prototype.getTotalPrice = function (id) {
    var total = 0;
    for (var i = 0; i < this.items.length; i++) {
        var item = this.items[i];
        if ((id == null || item._id == id) && item.selected === true) {
            total += this.toNumber(item.amountSelecteds * item.price);
        }
    }
    return total;
}

//get the total price for all items currently in the cart
shoppingCart.prototype.findOne = function (id) {
    for (var i = 0; i < this.items.length; i++) {
        var item = this.items[i];
        if (item._id == id) {
            return item;
        }
    }
    return null;
}

//adds an item to the cart
shoppingCart.prototype.addItem = function (id, name, quantity) {
    quantity = this.toNumber(quantity);
    var item = null;
    if (quantity != 0) {
        // update quantity for existing item
        for (var i = 0; i < this.items.length; i++) {
            item = this.items[i];
            if (item._id === id) {
                item.amount = this.toNumber(item.amount + quantity);
                if (item.amount < 0) {
                    //this.items.splice(i, 1);
                    item.amount = this.toNumber(item.amount - quantity);
                	var text = this.rootScope.getMessage("msg.error.amount.product", [name]);
                    this.rootScope.message.show(text);
                    return;
                }
                item.selected = true;
                item.amountSelecteds = isNullValue(item.amountSelecteds) ? (-1 * quantity) : item.amountSelecteds + (-1 * quantity);
                break;
            }
        }
    }
}

//save items to local storage
shoppingCart.prototype.saveItems = function () {
    this.store.set('product_itens', this.items);
}

shoppingCart.prototype.getItems = function () {
    this.store.get('product_itens');
}

shoppingCart.prototype.getShoppingItems = function () {
    var shoppingItems = [];
	for (var i = 0; i < this.items.length; i++) {
        var item = this.items[i];
        if (item.selected === true) {
        	shoppingItems.push(item);
        }
    }
    return shoppingItems;
}

//removes all items to the cart
shoppingCart.prototype.removeAllItem = function (id) {
    for (var i = 0; i < this.items.length; i++) {
        item = this.items[i];
        if (item._id === id || isNullValue(id)) {
            item.amount = this.toNumber(item.amount) + this.toNumber(item.amountSelecteds);
            item.amountSelecteds = 0;
            item.selected = false;
        }
    }
}

// load items from local storage
/*shoppingCart.prototype.loadItems = function () {
    var items = localStorage != null ? localStorage[this.cartName + "_items"] : null;
    if (items != null && JSON != null) {
        try {
            var items = JSON.parse(items);
            for (var i = 0; i < items.length; i++) {
                var item = items[i];
                if (item._id != null && item.name != null && item.price != null && item.amount != null) {
                    item = new cartItem(item._id, item.name, item.price, item.amount);
                    this.items.push(item);
                }
            }
        }
        catch (err) {
            // ignore errors while loading...
        }
    }
}*/















// define checkout parameters
shoppingCart.prototype.addCheckoutParameters = function (serviceName, merchantID, options) {

    // check parameters
    if (serviceName != "PayPal" && serviceName != "Google") {
        throw "serviceName must be 'PayPal' or 'Google'.";
    }
    if (merchantID == null) {
        throw "A merchantID is required in order to checkout.";
    }

    // save parameters
    this.checkoutParameters[serviceName] = new checkoutParameters(serviceName, merchantID, options);
}

// check out
shoppingCart.prototype.checkout = function (serviceName, clearCart) {

    // select serviceName if we have to
    if (serviceName == null) {
        var p = this.checkoutParameters[Object.keys(this.checkoutParameters)[0]];
        serviceName = p.serviceName;
    }

    // sanity
    if (serviceName == null) {
        throw "Use the 'addCheckoutParameters' method to define at least one checkout service.";
    }

    // go to work
    var parms = this.checkoutParameters[serviceName];
    if (parms == null) {
        throw "Cannot get checkout parameters for '" + serviceName + "'.";
    }
    switch (parms.serviceName) {
        case "PayPal":
            this.checkoutPayPal(parms, clearCart);
            break;
        case "Google":
            this.checkoutGoogle(parms, clearCart);
            break;
        default:
            throw "Unknown checkout service: " + parms.serviceName;
    }
}

// check out using PayPal
// for details see:
// www.paypal.com/cgi-bin/webscr?cmd=p/pdn/howto_checkout-outside
shoppingCart.prototype.checkoutPayPal = function (parms, clearCart) {

    // global data
    var data = {
        cmd: "_cart",
        business: parms.merchantID,
        upload: "1",
        rm: "2",
        charset: "utf-8"
    };

    // item data
    for (var i = 0; i < this.items.length; i++) {
        var item = this.items[i];
        var ctr = i + 1;
        data["item_number_" + ctr] = item.sku;
        data["item_name_" + ctr] = item.name;
        data["quantity_" + ctr] = item.quantity;
        data["amount_" + ctr] = item.price.toFixed(2);
    }

    // build form
    var form = $('<form/></form>');
    form.attr("action", "https://www.paypal.com/cgi-bin/webscr");
    form.attr("method", "POST");
    form.attr("style", "display:none;");
    this.addFormFields(form, data);
    this.addFormFields(form, parms.options);
    $("body").append(form);

    // submit form
    this.clearCart = clearCart == null || clearCart;
    form.submit();
    form.remove();
}

// check out using Google Wallet
// for details see:
// developers.google.com/checkout/developer/Google_Checkout_Custom_Cart_How_To_HTML
// developers.google.com/checkout/developer/interactive_demo
shoppingCart.prototype.checkoutGoogle = function (parms, clearCart) {

    // global data
    var data = {};

    // item data
    for (var i = 0; i < this.items.length; i++) {
        var item = this.items[i];
        var ctr = i + 1;
        data["item_name_" + ctr] = item.sku;
        data["item_description_" + ctr] = item.name;
        data["item_price_" + ctr] = item.price.toFixed(2);
        data["item_quantity_" + ctr] = item.quantity;
        data["item_merchant_id_" + ctr] = parms.merchantID;
    }

    // build form
    var form = $('<form/></form>');
    // NOTE: in production projects, use the checkout.google url below;
    // for debugging/testing, use the sandbox.google url instead.
    //form.attr("action", "https://checkout.google.com/api/checkout/v2/merchantCheckoutForm/Merchant/" + parms.merchantID);
    form.attr("action", "https://sandbox.google.com/checkout/api/checkout/v2/checkoutForm/Merchant/" + parms.merchantID);
    form.attr("method", "POST");
    form.attr("style", "display:none;");
    this.addFormFields(form, data);
    this.addFormFields(form, parms.options);
    $("body").append(form);

    // submit form
    this.clearCart = clearCart == null || clearCart;
    form.submit();
    form.remove();
}

// utility methods
shoppingCart.prototype.addFormFields = function (form, data) {
    if (data != null) {
        $.each(data, function (name, value) {
            if (value != null) {
                var input = $("<input></input>").attr("type", "hidden").attr("name", name).val(value);
                form.append(input);
            }
        });
    }
}
shoppingCart.prototype.toNumber = function (value) {
    value = value * 1;
    return isNaN(value) ? 0 : value;
}

//----------------------------------------------------------------
// checkout parameters (one per supported payment service)
//
function checkoutParameters(serviceName, merchantID, options) {
    this.serviceName = serviceName;
    this.merchantID = merchantID;
    this.options = options;
}

//----------------------------------------------------------------
// items in the cart
//
function cartItem(id, name, price, quantity) {
    this._id = id;
    this.name = name;
    this.price = price * 1;
    this.amount = quantity * 1;
}

