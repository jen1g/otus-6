# Используем официальный образ OpenJDK
FROM amazoncorretto:17

# Устанавливаем рабочую директорию в контейнере
WORKDIR /app

# Копируем build-артефакт (например, jar-файл) в контейнер
COPY target/*.jar /app/gateway.jar

# Указываем команду для запуска приложения
CMD ["java", "-jar", "/app/gateway.jar"]
