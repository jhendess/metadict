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

    // Time how long tasks take. Can help when optimizing build times
    require('time-grunt')(grunt);

    // Automatically load required Grunt tasks
    require('jit-grunt')(grunt, {
        useminPrepare: 'grunt-usemin',
        ngtemplates: 'grunt-angular-templates'
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

        // Clean various folders
        clean: {
            dist: {
                files: [{
                    dot: true,
                    src: [
                        '.tmp',
                        '<%= appConfig.paths.dist %>/{,*/}*',
                        '!<%= appConfig.paths.dist %>/.git{,*/}*'
                    ]
                }]
            },
            server: '.tmp',
            testSources : '.tmp/spec/app',
            dev: [
                "bower_components",
                "typings"
            ]
        },

        // Configure the grunt server for serving compiled files
        connect: {
            options: {
                port: 9000,
                // Change this to '0.0.0.0' to access the server from outside.
                hostname: 'localhost',
                livereload: 35729
            },
            livereload: {
                options: {
                    open: true,
                    middleware: function (connect) {
                        return [
                            serveStatic('.tmp'),
                            connect().use(
                                '/bower_components',
                                serveStatic('./bower_components')
                            ),
                            connect().use(
                                '/<%= appConfig.paths.app %>/styles',
                                serveStatic('./app/styles')
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
                    base: '<%= appConfig.paths.dist %>'
                }
            }
        },

        template : {
            dev : {
                options : {
                    data : {
                        config : appConfig.env.dev
                    }
                },
                files : {
                    '<%= appConfig.paths.app %>/scripts/Config.ts' : ['<%= appConfig.paths.app %>/scripts/Config.ts.tpl']
                }
            },
            prod : {
                options : {
                    data : {
                        config : appConfig.env.prod
                    }
                },
                files : {
                    '<%= appConfig.paths.app %>/scripts/Config.ts' : ['<%= appConfig.paths.app %>/scripts/Config.ts.tpl']
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
                files: ['<%= appConfig.paths.app %>/scripts/{,*/}*.ts'],
                tasks: ['typescript:base']
            },
            typescriptTest: {
                files: ['test/spec/{,*/}*.ts'],
                tasks: ['typescript:test', 'karma']
            },
            styles: {
                files: ['<%= appConfig.paths.app %>/styles/{,*/}*.css'],
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
                    '<%= appConfig.paths.app %>/{,*/}*.html',
                    '.tmp/styles/{,*/}*.css',
                    '.tmp/scripts/{,*/}*.js',
                    '<%= appConfig.paths.app %>/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}'
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
                src: ['<%= appConfig.paths.app %>/scripts/{,*/}*.ts'],
                dest: '.tmp/scripts',
                options: {
                    module: 'amd', //or commonjs
                    target: 'es5', //or es3
                    'base_path': '<%= appConfig.paths.app %>/scripts', //quoting base_path to get around jshint warning.
                    sourcemap: true,
                    declaration: true
                }
            },
            test: {
                src: ['test/spec/{,*/}*.ts', 'test/e2e/{,*/}*.ts'],
                dest: '.tmp/spec',
                options: {
                    module: 'amd', //or commonjs
                    target: 'es5', //or es3
                    sourcemap: true,
                    declaration: true
                }
            }
        },

        // Install typescript type definitions
        tsd: {
            refresh: {
                options: {
                    // execute a command
                    command: 'reinstall',
                    config: 'tsd.json'
                }
            }
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
                        'views/*.html',
                        'images/{,*/}*.*',
                        'styles/fonts/{,*/}*.*'
                    ]
                }]
            },
            styles: {
                expand: true,
                cwd: '<%= appConfig.paths.app %>/styles',
                dest: '.tmp/styles/',
                src: '{,*/}*.css'
            },
            distSources : {
                // Copy from dist to .tmp, to allow testing of minified files
                expand: true,
                cwd: '<%= appConfig.paths.dist %>',
                src: '{,*/}*.*',
                dest: '.tmp/spec'
            }
        },

        // Renames files for browser caching purposes
        filerev: {
            dist: {
                src: [
                    '<%= appConfig.paths.dist %>/scripts/{,*/}*.js',
                    '<%= appConfig.paths.dist %>/styles/{,*/}*.css',
                    '<%= appConfig.paths.dist %>/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}',
                    '<%= appConfig.paths.dist %>/styles/fonts/*'
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
            html: ['<%= appConfig.paths.dist %>/{,*/}*.html'],
            css: ['<%= appConfig.paths.dist %>/styles/{,*/}*.css'],
            js: ['<%= appConfig.paths.dist %>/scripts/{,*/}*.js'],
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
                'copy:styles'
            ],
            test: [
                'typescript:test',
                'typescript:base',
                'copy:styles'
            ],
            dist: [
                'typescript:base',
                'copy:styles'
            ]
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
        "tsd:refresh"
    ]);

    grunt.registerTask("check", [
        "tslint"
    ]);

    grunt.registerTask('serve', [
        'clean:server',
        'wiredep',
        'tsd:refresh',
        'template:dev',
        'concurrent:server',
        'connect:livereload',
        'watch'
    ]);

    grunt.registerTask('test', [
        'clean:server',
        'wiredep',
        'template:dev',
        'tsd:refresh',
        'concurrent:test',
        'connect:test',
        'karma'
    ]);

    grunt.registerTask('build', [
        'clean:dist',
        'wiredep',
        'tsd:refresh',
        'template:prod',
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
        'htmlmin'
    ]);

    grunt.registerTask('build:clean', [
        "setup",
        "build"
    ]);

    grunt.registerTask('serve:dist', ["build", "connect:dist", "wait-forever"]);

    grunt.registerTask('test:dist', [
        "build",
        "clean:server",
        "typescript:test",
        "clean:testSources",
        "copy:distSources",
        "karma"
    ]);

    grunt.registerTask('default', [
        "check",
        "test:dist"
    ])

};

