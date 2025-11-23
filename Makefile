JAVA_FX = ./javafx-sdk/javafx-sdk-17.0.17/lib
MODULES = javafx.controls,javafx.graphics
SRC = src/app/*.java src/logic/*.java src/model/*.java src/ui/*.java

install:
	@test -d javafx-sdk || [ -f javafx-sdk.zip ] || wget -q https://download2.gluonhq.com/openjfx/17.0.17/openjfx-17.0.17_linux-x64_bin-sdk.zip -O javafx-sdk.zip
	@test -d javafx-sdk || unzip -q javafx-sdk.zip -d ./javafx-sdk
	@rm -f javafx-sdk.zip

compile:
	javac --module-path $(JAVA_FX) --add-modules $(MODULES) -d bin $(SRC)

run: compile
	java --module-path $(JAVA_FX) --add-modules $(MODULES) -cp bin app.Main

clean:
	rm -rf bin
