FROM mobingi/ubuntu-apache2-php7:7.2

WORKDIR /var/lib/

RUN apt-get update && apt-get install git curl -y && \
    mkdir -p /var/lib/mediawiki && \
    curl https://releases.wikimedia.org/mediawiki/1.31/mediawiki-1.31.1.tar.gz \
    --output mediawiki-1.31.1.tar.gz && \
    tar zxf mediawiki-1.31.1.tar.gz -C /var/lib/ && \
    mv /var/lib/mediawiki-1.31.1 /var/lib/mediawiki && \
    mv /var/lib/mediawiki/mediawiki-1.31.1/* /var/lib/mediawiki && \
    rm -rf /var/lib/mediawiki/mediawiki-1.31.1 && \
    ln -s /var/lib/mediawiki /var/www/html/mediawiki

EXPOSE 80
EXPOSE 443

CMD ["apache2ctl","-D","FOREGROUND"]

