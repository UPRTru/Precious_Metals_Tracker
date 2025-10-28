FROM openjdk:17-jdk

# Установка Chrome и ChromeDriver
RUN apt-get update && apt-get install -y \
    wget \
    gnupg \
    ca-certificates \
    unzip \
    xvfb \
    libnss3-tools \
    && wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# Установка ChromeDriver (подберите версию под ваш Chrome)
ENV CHROMEDRIVER_VERSION=141.0.7390.122
RUN wget -O /tmp/chromedriver.zip https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/${CHROMEDRIVER_VERSION}/linux64/chromedriver-linux64.zip \
    && unzip /tmp/chromedriver.zip -d /opt/ \
    && mv /opt/chromedriver-linux64/chromedriver /usr/local/bin/chromedriver \
    && chmod +x /usr/local/bin/chromedriver

# Копируем сертификаты
COPY rt_ca_cer/*.cer /usr/local/share/ca-certificates/

# Импортируем сертификаты в системное хранилище
RUN chmod 644 /usr/local/share/ca-certificates/*.cer \
    && update-ca-certificates

# Копируем ваше приложение
COPY your-app.jar /app.jar

CMD ["java", "-jar", "/app.jar"]