set -e

APP="/app/"

DIR_BASE="`pwd`"
mvn clean package -Pmake-package

cd $DIR_BASE
cd bulid
chmod 777 hdata-0.2.8.tar.gz

rm -rf /app/hdata-0.2.8.tar.gz
cp hdata-0.2.8.tar.gz $APP

cd $APP
rm -rf hdata-0.2.8
tar -xvf  hdata-0.2.8.tar.gz
cp mysql-connector-java-5.1.45.jar hdata-0.2.8/plugins/jdbc/