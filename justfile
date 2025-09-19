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
            VERSION=$(curl -s https://api.papermc.io/v2/projects/paper | jq -r '[.versions[] | select(test("^[0-9]+\\.[0-9]+\\.[0-9]+$"))] | last'); \
            curl -s https://api.papermc.io/v2/projects/paper/versions/$VERSION/builds | jq -r '.builds[-1].build' | xargs -I {build} curl -O https://api.papermc.io/v2/projects/paper/versions/$VERSION/builds/{build}/downloads/paper-$VERSION-{build}.jar; \
            mv paper-*.jar paper.jar; \
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

nuke:
    rm -rf dev-server
    rm -rf target 