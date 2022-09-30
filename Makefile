run: assemble
	jekyll serve

assemble:
	./gradlew bundleJs
	cp -r demo-camera/build/distributions js/
	cp -r demo-dance/build/distributions js/
	cp -r demo-imgui-light/build/distributions js/
	cp -r demo-physic/build/distributions js/
	cp -r demo-shader/build/distributions js/
	cp -r demo-text/build/distributions js/
	cp -r demo-threed/build/distributions js/
	cp -r demo-twod/build/distributions js/

deploy: assemble
	jekyll build
