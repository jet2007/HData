set -e

APP="/app/"

DIR_BASE="`pwd`"
mvn clean package -Pmake-package

cd $DIR_BASE
cd build
chmod 777 hdata-1.0.0.tar.gz

rm -rf $APP/hdata-1.0.0.tar.gz
cp hdata-1.0.0.tar.gz $APP

cd $APP
rm -rf hdata-1.0.0
tar -xvf  hdata-1.0.0.tar.gz
#cp mysql-connector-java-5.1.45.jar hdata-1.0.0/plugins/jdbc/