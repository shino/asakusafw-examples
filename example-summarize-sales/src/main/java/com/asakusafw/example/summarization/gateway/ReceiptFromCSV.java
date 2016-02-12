/**
 * Copyright 2011-2016 Asakusa Framework Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.asakusafw.example.summarization.gateway;

import com.asakusafw.example.summarization.modelgen.dmdl.csv.AbstractReceiptCsvInputDescription;

/**
 * レシートデータをDirect I/Oで入力する。
 * 入力ファイルは {@code input/receipt.csv}。
 */
public class ReceiptFromCSV extends AbstractReceiptCsvInputDescription {

	@Override
	public String getBasePath() {
        return "input";
	}

	@Override
	public String getResourcePattern() {
        return "receipt.csv";
	}
	
}
