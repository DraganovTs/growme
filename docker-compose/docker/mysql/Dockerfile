FROM mysql:8.0

# Copy config file into image
COPY ./conf.d/my.cnf /etc/mysql/conf.d/my.cnf

# Set correct permissions inside the image
RUN chmod 644 /etc/mysql/conf.d/my.cnf
