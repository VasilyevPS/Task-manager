setup:
	make -C app setup

clean:
	make -C app clean

build:
	make -C app build

start:
	make -C app start

start-prod:
	make -C app start-prod

lint:
	make -C app lint

test:
	make -C app test

report:
	make -C app report


.PHONY: build