# Justfile for Afterlife Plugin Development

# Usage: just setup-server

check-deps:
    asdf install

setup-server: check-deps
    if [ ! -d dev-server ]; then \
        mkdir -p dev-server; \
    fi
    if [ ! -f dev-server/paper.jar ]; then \
        (cd dev-server && \
            curl -s https://api.papermc.io/v2/projects/paper | jq -r '.versions[-1]' | xargs -I {} curl -s https://api.papermc.io/v2/projects/paper/versions/{}/builds | jq -r '.builds[-1].build' | xargs -I {build} sh -c 'curl -O https://api.papermc.io/v2/projects/paper/versions/$(curl -s https://api.papermc.io/v2/projects/paper | jq -r ".versions[-1]")/builds/{build}/downloads/paper-$(curl -s https://api.papermc.io/v2/projects/paper | jq -r ".versions[-1]")-{build}.jar && mv paper-*.jar paper.jar'; \
            echo 'eula=true' > eula.txt \
        ); \
    fi

deploy: check-deps
    mvn clean package
    mkdir -p dev-server/plugins
    cp target/AfterLifePlugin-1.0-SNAPSHOT.jar dev-server/plugins/

start-server: deploy
    if [ ! -f dev-server/paper.jar ]; then \
        just setup-server; \
    fi
    cd dev-server && java -Xms2G -Xmx4G -jar paper.jar nogui

# Test commands
test test_class="":
    #!/usr/bin/env bash
    echo "Usage: just test [test_class]"
    echo "Examples:"
    echo "  just test                    # Run all tests"
    echo "  just test AfterlifePersistenceTest  # Run specific test"
    echo ""
    if [ -z "{{test_class}}" ]; then
        echo "Running all tests..."
        mvn test
    else
        echo "Running test: {{test_class}}"
        mvn test -Dtest={{test_class}}
    fi

nuke:
    rm -rf dev-server
    rm -rf target 