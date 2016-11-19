'use strict';
module.exports = function(grunt) {
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),

		clean: {
		   toProtocoder: {
		    	src: [ '../Protocoder/protocoder_app/src/main/assets/examples/**' ],
					
			    options: {
			      force: true
			    },
		   },
		},

        copy: {
	      toProtocoder: {
	      	cwd: 'included_examples',
	      	src: ['**'],
	      	dest: '../Protocoder/protocoder_app/src/main/assets/examples',
	      	expand: true
	      }
	    },
		watch: {
		  scripts: {
		    files: 'included_examples/**',
		    tasks: [ 'copy' ]
		  },
		},

    });

	grunt.loadNpmTasks('grunt-contrib-copy');
	grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-watch');

	grunt.registerTask(
		'default', 
		'build and creates a server that check for changes', 
		['watch']
	); 

	grunt.registerTask(
		'deploy', 
		'build and copies to the Protocoder folder', 
		['clean','copy:toProtocoder']
	); 


};