Decimal issue of Parquet written by Asakusa Direct IO
=====================================================

問題: Decimal フィールドが hive 側で NULL に見える。

アプリ
======

- モデルは key: text, value:decimal
- CSV 入力を読み込む
- 同じフィールド定義で parquet を出力する

CSV 入力サンプル作成

```
$ mkdir -p ~/target/testing/directio/in_csv/
$ echo "aaaa,100" > ~/target/testing/directio/in_csv/a.csv
```

環境
====

- Asakusafw 0.9.0 or 0.9.1-SNAPSHOT
- on MacOSX, java `version "1.8.0_91"`
- Asakusa on Vanilla

Hive での Parquet 読み込み on CDH
=================================

CDH 5.8.0 (`Hadoop 2.6.0-cdh5.8.0`)

```
[cloudera@quickstart ~]$ hive
hive> drop table if exists out_parq;
OK
Time taken: 0.382 seconds
hive> CREATE EXTERNAL TABLE IF NOT EXISTS out_parq (
    >     key STRING COMMENT 'key' ,
    >     dec DECIMAL(10, 0) COMMENT 'dec'
    > )
    > COMMENT 'out_parq'
    > STORED AS PARQUET
    > location '/user/cloudera/out_parq';
OK
Time taken: 0.116 seconds

hive> select * from out_parq;
aaaa    NULL
Time taken: 0.391 seconds, Fetched: 1 row(s)
```

Impala での Parquet 読み込み on CDH
===================================

```
[cloudera@quickstart ~]$ impala-shell
Starting Impala Shell without Kerberos authentication
Connected to quickstart.cloudera:21000
Server version: impalad version 2.6.0-cdh5.8.0 RELEASE (build 5464d1750381b40a7e7163b12b09f11b891b4de3)

[quickstart.cloudera:21000] > select * from out_parq;
Query: select * from out_parq
WARNINGS:
File 'hdfs://quickstart.cloudera:8020/user/cloudera/out_parq/v1-1.parq' has an incompatible Parquet schema for column 'default.out_parq.dec'. Column type: DECIMAL(10, 0), Parquet schema:
optional byte_array value [i:1 d:1 r:0]

File 'hdfs://quickstart.cloudera:8020/user/cloudera/out_parq/v1-1.parq' has an incompatible Parquet schema for column 'default.out_parq.dec'. Column type: DECIMAL(10, 0), Parquet schema:
optional byte_array value [i:1 d:1 r:0]
```

`hive.libraries` の設定
=======================

build.gradle に設定を追加:

```
repositories {
    maven { url 'https://repository.cloudera.com/artifactory/cloudera-repos/' }
    maven { url "http://conjars.org/repo" }
}

asakusafwOrganizer {
    [snip]
    hive.libraries = ['org.apache.hive:hive-exec:1.1.0-cdh5.8.0']
    [snip]
}
```

バッチ実行時にエラーが発生:
`Exception in thread "main" java.lang.NoSuchMethodError: parquet.schema.Types$PrimitiveBuilder.precision(I)Lparquet/schema/Types$PrimitiveBuilder;`

以下に抜粋。全ログは `yaess-error-hive-exec.log` 。

```
12:47:50 INFO      number of output records: 0
12:47:50 INFO      output file size in bytes: 0
Exception in thread "main" java.lang.NoSuchMethodError: parquet.schema.Types$PrimitiveBuilder.precision(I)Lparquet/schema/Types$PrimitiveBuilder;
        at com.asakusafw.directio.hive.parquet.DecimalValueDriver.getType(DecimalValueDriver.java:80)
        at com.asakusafw.directio.hive.parquet.DataModelWriteSupport.computeParquetType(DataModelWriteSupport.java:101)
        at com.asakusafw.directio.hive.parquet.DataModelWriteSupport.computeSchema(DataModelWriteSupport.java:89)
        at com.asakusafw.directio.hive.parquet.DataModelWriteSupport.<init>(DataModelWriteSupport.java:73)
        at com.asakusafw.directio.hive.parquet.DataModelWriteSupport.<init>(DataModelWriteSupport.java:59)
        at com.asakusafw.directio.hive.parquet.ParquetFileOutput.<init>(ParquetFileOutput.java:79)
        at com.asakusafw.directio.hive.parquet.AbstractParquetFileFormat.createOutput(AbstractParquetFileFormat.java:240)
        at com.asakusafw.runtime.directio.hadoop.HadoopDataSourceCore.openOutput(HadoopDataSourceCore.java:289)
        at com.asakusafw.runtime.directio.hadoop.HadoopDataSource.openOutput(HadoopDataSource.java:116)
        at com.asakusafw.dag.runtime.directio.DirectFileOutputDriver.newInstance(DirectFileOutputDriver.java:80)
        at com.asakusafw.dag.runtime.directio.DirectFileOutputPrepare.lambda$initialize$1(DirectFileOutputPrepare.java:116)
        at com.asakusafw.dag.runtime.directio.DirectFileOutputPrepare.createTaskProcessor(DirectFileOutputPrepare.java:147)
        at com.asakusafw.vanilla.core.engine.VertexExecutor$TaskExecutor.run(VertexExecutor.java:396)
        at com.asakusafw.vanilla.core.engine.VertexExecutor.lambda$null$0(VertexExecutor.java:184)
        at java.util.concurrent.FutureTask.run(FutureTask.java:266)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
        at java.lang.Thread.run(Thread.java:745)
```

