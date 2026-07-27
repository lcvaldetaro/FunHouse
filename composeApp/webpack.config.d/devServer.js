if (config.devServer) {
    config.devServer.open = ['/funhouse.html'];
    if (config.devServer.static) {
        if (Array.isArray(config.devServer.static)) {
            config.devServer.static.forEach(function(s) {
                if (s && typeof s === 'object') {
                    s.staticOptions = s.staticOptions || {};
                    s.staticOptions.index = 'funhouse.html';
                }
            });
        } else if (typeof config.devServer.static === 'object') {
            config.devServer.static.staticOptions = config.devServer.static.staticOptions || {};
            config.devServer.static.staticOptions.index = 'funhouse.html';
        } else if (typeof config.devServer.static === 'string') {
            config.devServer.static = {
                directory: config.devServer.static,
                staticOptions: {
                    index: 'funhouse.html'
                }
            };
        }
    } else {
        config.devServer.static = {
            staticOptions: {
                index: 'funhouse.html'
            }
        };
    }
}
