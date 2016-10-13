/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Jakob Hendeß
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

'use strict';

module.exports = function (grunt) {

    // Load package.json
    var pkg = grunt.file.readJSON("package.json");

    // Load modRewrite for using angular's html5mode in grunt:serve
    var modRewrite = require('connect-modrewrite');

    // Use "--clientBasePath=/something" to set /something as the angular basepath for html5mode
    var clientBasePath = grunt.option("clientBasePath");

    if (!clientBasePath || clientBasePath === "") {
        clientBasePath = "/";
    }

    // Time how long tasks take. Can help when optimizing build times
    require('time-grunt')(grunt);

    // Automatically load required Grunt tasks
    require('jit-grunt')(grunt, {
        useminPrepare: 'grunt-usemin',
        ngtemplates: 'grunt-angular-templates',
        revision: 'grunt-git-revision'
    });

    // Allow serving of static content (i.e. everything)
    var serveStatic = require('serve-static');

    // Load additional configuration values from external file
    var appConfig = grunt.file.readJSON("config.json");

    // Configure tasks
    grunt.initConfig({

        appConfig: appConfig,

        // Configure how Grunt should install bower dependencies
        "bower-install-simple": {
            options: {
                color: true
            },
            "prod": {
                options: {
                    production: true
                }
            },
            "dev": {
                options: {
                    production: false
                }
            }
        },

        // Store the git revision to property meta.revision
        revision: {
            options: {
                property: 'meta.revision',
                ref: 'HEAD',
                short: true
            }
        },

        // Clean various folders
        clean: {
            options: {
                "force": true
            },
            dist: {
                files: [{
                    dot: true,
                    src: [
                        '.tmp',
                        '<%= appConfig.paths.dist %>/**/*',
                        '!<%= appConfig.paths.dist %>/.git**/*'
                    ]
                }]
            },
            server: '.tmp',
            testSources: '.tmp/spec/app',
            dev: [
                "bower_components",
                "typings"
            ]
        },

        // Configure the grunt server for serving compiled files
        connect: {
            options: {
                port: 9000,
                livereload: 35729
            },
            livereload: {
                options: {
                    open: true,
                    middleware: function (connect) {
                        return [
                            modRewrite(['^[^\\.]*$ /index.html [L]']),
                            serveStatic('.tmp'),
                            connect().use(
                                '/bower_components',
                                serveStatic('./bower_components')
                            ),
                            connect().use(
                                '/app',
                                serveStatic('./app')
                            ),
                            serveStatic(appConfig.paths.app)
                        ];
                    }
                }
            },
            test: {
                options: {
                    port: 9001,
                    middleware: function (connect) {
                        return [
                            serveStatic('.tmp'),
                            serveStatic('test'),
                            connect().use(
                                '/bower_components',
                                serveStatic('./bower_components')
                            ),
                            serveStatic('./<%= appConfig.paths.app %>')
                        ];
                    }
                }
            },
            dist: {
                options: {
                    open: true,
                    middleware: function (connect) {
                        return [
                            modRewrite(['^[^\\.]*$ /index.html [L]']),
                            serveStatic(appConfig.paths.dist)
                        ]
                    }
                }
            }
        },

        template: {
            dev: {
                options: {
                    data: {
                        config: appConfig.env.dev,
                        pkg: pkg,
                        revision: "<%= meta.revision %>",
                        clientBasePath: "/"
                    }
                },
                files: {
                    '<%= appConfig.paths.app %>/scripts/Config.ts': ['<%= appConfig.paths.app %>/scripts/Config.ts.tpl']
                }
            },
            prod: {
                options: {
                    data: {
                        config: appConfig.env.prod,
                        pkg: pkg,
                        revision: "<%= meta.revision %>",
                        clientBasePath: clientBasePath
                    }
                },
                files: {
                    '<%= appConfig.paths.app %>/scripts/Config.ts': ['<%= appConfig.paths.app %>/scripts/Config.ts.tpl']
                }
            }
        },

        // Watches files for changes and runs tasks based on the changed files
        watch: {
            bower: {
                files: ['bower.json'],
                tasks: ['wiredep']
            },
            typescript: {
                files: ['<%= appConfig.paths.app %>/scripts/**/*.ts'],
                tasks: ['typescript:base']
            },
            typescriptTest: {
                files: ['test/spec/**/*.ts'],
                tasks: ['typescript:test', 'karma']
            },
            styles: {
                files: ['<%= appConfig.paths.app %>/styles/**/*.css'],
                tasks: ['newer:copy:styles']
            },
            gruntfile: {
                files: ['Gruntfile.js']
            },
            livereload: {
                options: {
                    livereload: '<%= connect.options.livereload %>'
                },
                files: [
                    '<%= appConfig.paths.app %>/**/*.html',
                    '.tmp/styles/**/*.css',
                    '.tmp/scripts/**/*.js',
                    '<%= appConfig.paths.app %>/images/**/*.{png,jpg,jpeg,gif,webp,svg}'
                ]
            }
        },

        // Automatically inject Bower components into the app
        wiredep: {
            app: {
                src: ['<%= appConfig.paths.app %>/index.html'],
                ignorePath: /\.\.\//
            },
            // Configuration for injecting components into Karma config
            test: {
                devDependencies: true,
                src: '<%= karma.unit.configFile %>',
                ignorePath: /\.\.\//,
                fileTypes: {
                    js: {
                        // Begin block with "//  bower:js" and end block with "// endbower"
                        block: /(([\s\t]*)\/{2}\s*?bower:\s*?(\S*))(\n|\r|.)*?(\/{2}\s*endbower)/gi,
                        detect: {
                            js: /'(.*\.js)'/gi
                        },
                        replace: {
                            js: '\'{{filePath}}\','
                        }
                    }
                }
            }
        },

        // Compiles TypeScript to JavaScript
        typescript: {
            base: {
                src: ['<%= appConfig.paths.app %>/scripts/**/*.ts'],
                dest: '.tmp/scripts',
                options: {
                    module: 'amd', //or commonjs
                    target: 'es5', //or es3
                    'base_path': '<%= appConfig.paths.app %>/scripts', //quoting base_path to get around jshint warning.
                    sourceMap: true,
                    declaration: true
                }
            },
            test: {
                src: ['test/spec/**/*.ts', 'test/e2e/**/*.ts'],
                dest: '.tmp/spec',
                options: {
                    module: 'amd', //or commonjs
                    target: 'es5', //or es3
                    sourceMap: true,
                    declaration: true
                }
            }
        },

        // Install typescript type definitions
        typings: {
            install: {}
        },

        // Copy remaining files to places other tasks can use
        copy: {
            dist: {
                files: [{
                    expand: true,
                    dot: true,
                    cwd: '<%= appConfig.paths.app %>',
                    dest: '<%= appConfig.paths.dist %>',
                    src: [
                        '*.{ico,png,txt}',
                        '*.html',
                        'images/**/*.*',
                        '/fonts/**/*.*'
                    ]
                }, {
                    expand: true,
                    cwd: "bower_components/roboto-fontface/fonts/",
                    src: "roboto/*-{Thin,Light,Regular,Medium,Bold}.{eot,woff2,woff,ttf}",
                    dest: "dist/fonts"
                }, {
                    expand: true,
                    cwd: "bower_components/material-design-icons/iconfont/",
                    src: ["*.{eot,woff,woff2,ttf,ijmap}"],
                    dest: "dist/fonts"
                }]
            },
            styles: {
                expand: true,
                cwd: '<%= appConfig.paths.app %>/styles',
                dest: '.tmp/styles/',
                src: '**/*.css'
            },
            distSources: {
                // Copy from dist to .tmp, to allow testing of minified files
                expand: true,
                cwd: '<%= appConfig.paths.dist %>',
                src: '**/*.*',
                dest: '.tmp/spec'
            },
            flags: {
                expand: true,
                cwd: "bower_components/flag-icon-css/flags",
                src: ['**/*.svg'],
                dest: '.tmp/flags/'
            },
            icons: {
                expand: true,
                cwd: "bower_components/material-design-icons/iconfont/",
                src: ["*.{eot,woff,woff2,ttf,ijmap}"],
                dest: ".tmp/fonts"
            },
            roboto: {
                expand: true,
                cwd: "bower_components/roboto-fontface/fonts/",
                src: "roboto/*-{Thin,Light,Regular,Medium,Bold}.{eot,woff2,woff,ttf}",
                dest: ".tmp/fonts"
            }
        },

        // Renames files for browser caching purposes
        filerev: {
            dist: {
                src: [
                    '<%= appConfig.paths.dist %>/scripts/**/*.js',
                    '<%= appConfig.paths.dist %>/styles/**/*.css',
                    '<%= appConfig.paths.dist %>/images/**/*.{png,jpg,jpeg,gif,webp,svg}',
                    '<%= appConfig.paths.dist %>/fonts/*'
                ]
            }
        },

        // Reads HTML for usemin blocks to enable smart builds that automatically
        // concat, minify and revision files. Creates configurations in memory so
        // additional tasks can operate on them
        useminPrepare: {
            html: '<%= appConfig.paths.app %>/index.html',
            options: {
                dest: '<%= appConfig.paths.dist %>',
                flow: {
                    html: {
                        steps: {
                            js: ['concat', 'uglifyjs'],
                            css: ['cssmin']
                        },
                        post: {}
                    }
                }
            }
        },

        // Performs rewrites based on filerev and the useminPrepare configuration
        usemin: {
            html: ['<%= appConfig.paths.dist %>/**/*.html'],
            css: ['<%= appConfig.paths.dist %>/styles/**/*.css'],
            js: ['<%= appConfig.paths.dist %>/scripts/**/*.js'],
            options: {
                assetsDirs: [
                    '<%= appConfig.paths.dist %>',
                    '<%= appConfig.paths.dist %>/images',
                    '<%= appConfig.paths.dist %>/styles'
                ],
                patterns: {
                    js: [[/(images\/[^''""]*\.(png|jpg|jpeg|gif|webp|svg))/g, 'Replacing references to images']]
                }
            }
        },

        // Minify HTML
        htmlmin: {
            dist: {
                options: {
                    collapseWhitespace: true,
                    conservativeCollapse: true,
                    collapseBooleanAttributes: true,
                    removeCommentsFromCDATA: true
                },
                files: [{
                    expand: true,
                    cwd: '<%= appConfig.paths.dist %>',
                    src: ['*.html', 'views/*.html'],
                    dest: '<%= appConfig.paths.dist %>'
                }]
            }
        },

        //  Minify the svg flag icons
        svgmin: {
            dist: {
                files: [{
                    expand: true,
                    cwd: "bower_components/flag-icon-css/flags",
                    src: ['**/*.svg'],
                    dest: 'dist/flags/',
                    ext: '.svg'
                }]
            }
        },

        // Move AngularJS templates into template cache -> prevents loading times when switching views
        ngtemplates: {
            dist: {
                options: {
                    module: 'MetadictApp',
                    htmlmin: '<%= htmlmin.dist.options %>',
                    usemin: 'scripts/scripts.js'
                },
                cwd: '<%= appConfig.paths.app %>',
                src: 'views/*.html',
                dest: '.tmp/templateCache.js'
            }
        },

        // ng-annotate tries to make the code safe for minification automatically
        // by using the Angular long form for dependency injection.
        ngAnnotate: {
            dist: {
                files: [{
                    expand: true,
                    cwd: '.tmp/concat/scripts',
                    src: '*.js',
                    dest: '.tmp/concat/scripts'
                }]
            }
        },

        // Test settings
        karma: {
            unit: {
                configFile: 'test/karma.conf.js',
                singleRun: true
            }
        },

        // Run some tasks in parallel to speed up the build process
        concurrent: {
            server: [
                'typescript:base',
                'copy:styles',
                'copy:flags',
                'copy:icons',
                'copy:roboto'
            ],
            test: [
                'typescript:test',
                'typescript:base',
                'copy:styles'
            ],
            dist: [
                'svgmin',
                'typescript:base',
                'copy:styles'
            ]
        },

        // Generate an appcache manifest
        appcache: {
            options: {
                basePath: 'dist'
            },
            all: {
                dest: 'dist/manifest.appcache',
                cache: {
                    patterns: [
                        'dist/scripts/*.js',
                        'dist/styles/*.css',
                        'dist/fonts/**/*'
                    ]
                },
                network: '*'
            }
        },

        // Code quality check with TSLint
        tslint: {
            options: {
                // can be a configuration object or a filepath to tslint.json
                configuration: "tslint.json"
            },
            files: {
                src: [
                    "<%= appConfig.paths.app %>/scripts/**/*.ts"
                ]
            }
        }
    });

    // Register tasks
    grunt.registerTask("setup", ["clean:dev",
        "bower-install-simple",
        "typings:install"
    ]);

    grunt.registerTask("check", [
        "tslint"
    ]);

    grunt.registerTask('serve', [
        'clean:server',
        'wiredep',
        //'typings:install',
        'revision',
        'template:dev',
        'concurrent:server',
        'connect:livereload',
        'watch'
    ]);

    grunt.registerTask('test', [
        'clean:server',
        'wiredep',
        'revision',
        'template:dev',
        'typings:install',
        'concurrent:test',
        'connect:test',
        'karma'
    ]);

    grunt.registerTask('build', function (target) {
        grunt.log.writeln("Using '" + clientBasePath + "' as base path for angular application");

        grunt.task.run([
            'clean:dist',
            'wiredep',
            'typings:install',
            'revision'
        ]);

        if (target === "serve") {
            grunt.task.run("template:dev");
        } else {
            grunt.task.run("template:prod");
        }

        grunt.task.run([
            'useminPrepare',
            'concurrent:dist',
            'ngtemplates',
            'concat',
            'ngAnnotate',
            'copy:dist',
            'cssmin',
            'uglify',
            'filerev',
            'usemin',
            'htmlmin',
            'appcache'
        ]);

        if (target === "test") {
            grunt.task.run([
                "typescript:test",
                "clean:testSources",
                "copy:distSources",
                "karma"
            ]);
        } else if (target === "serve") {
            grunt.task.run([
                "connect:dist",
                "wait-forever"
            ]);
        }
    });

    grunt.registerTask('build:clean', [
        "setup",
        "build"
    ]);

    grunt.registerTask('default', [
        "build:test"
    ])

};

