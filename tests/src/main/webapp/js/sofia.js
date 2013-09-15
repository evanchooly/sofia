var sofiaBundles = [];
    sofiaBundles[''] = {
        'another' : 'I\'m an error',
        'date.property' : 'Today\'\'s date {0,date,full} and now a number {1,number}',
        'date.property2' : 'Today\'\'s date {0} and now a number {1}',
        'me' : 'I\'m just a warning, though.',
        'lonely' : 'I\'m only in the default bundle.',
        'new.property' : 'New Property',
        'parameterized.property.long.name' : 'I need parameters {0} and {1}',
        'test.property' : 'I\'m the first test property'
    };
    sofiaBundles['de'] = {
        'date.property' : 'Today\'s date is {0,date,full} and now a number {1,number}',
        'new.property' : 'Neue Propertische',
        'parameterized.property.long.name' : 'I need zwei parameters {0} and {1}',
        'test.property' : 'I\'m zee first test property'
    };
    sofiaBundles['en_GB'] = {
        'date.property' : 'Today\'s date is {0,date,full} and now a number {1,number}',
        'new.property' : 'New Guy',
        'parameterized.property.long.name' : 'I need two parameters {0} and {1}',
        'test.property' : 'I\'m the first test property, bloke'
    };
    sofiaBundles['en_US'] = {
        'another' : 'I\'m an error',
        'date.property' : 'Today\'\'s date {0,date,full} and now a number {1,number}',
        'date.property2' : 'Today\'\'s date {0} and now a number {1}',
        'me' : 'I\'m just a warning, though.',
        'lonely' : 'I\'m only in the default bundle.',
        'new.property' : 'New Property',
        'parameterized.property.long.name' : 'I need parameters {0} and {1}',
        'test.property' : 'I\'m the first test property'
    };

var sofiaLang = navigator.language || navigator.userLanguage;
sofia = {
    format: function(value, arguments) {
        var formatted = value;
        if (arguments) {
            for (var arg in arguments) {
                formatted = formatted.replace("{" + arg + "}", arguments[arg]);
            }
        }
        return formatted;
    },
    another: function() {
        return format(sofiaBundles[sofiaLang]['@error.another']);
    },
    dateProperty: function(arg0, arg1) {
        return format(sofiaBundles[sofiaLang]['@error.date.property']);
    },
    dateProperty2: function(arg0, arg1) {
        return format(sofiaBundles[sofiaLang]['@error.date.property2']);
    },
    me: function() {
        return format(sofiaBundles[sofiaLang]['@warn.me']);
    },
    lonely: function() {
        return format(sofiaBundles[sofiaLang]['lonely']);
    },
    newProperty: function() {
        return format(sofiaBundles[sofiaLang]['new.property']);
    },
    parameterizedPropertyLongName: function(arg0, arg1) {
        return format(sofiaBundles[sofiaLang]['parameterized.property.long.name']);
    },
    testProperty: function() {
        return format(sofiaBundles[sofiaLang]['test.property']);
    }
};
