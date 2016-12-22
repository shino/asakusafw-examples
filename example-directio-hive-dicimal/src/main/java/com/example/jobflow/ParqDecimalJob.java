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
package com.example.jobflow;

import com.asakusafw.vocabulary.flow.Export;
import com.asakusafw.vocabulary.flow.FlowDescription;
import com.asakusafw.vocabulary.flow.Import;
import com.asakusafw.vocabulary.flow.In;
import com.asakusafw.vocabulary.flow.JobFlow;
import com.asakusafw.vocabulary.flow.Out;
import com.asakusafw.vocabulary.flow.util.CoreOperatorFactory;
import com.example.modelgen.dmdl.model.InCsv;
import com.example.modelgen.dmdl.model.OutParq;

/**
 * カテゴリ別に売上の集計を計算する。
 */
@JobFlow(name = "convert")
public class ParqDecimalJob extends FlowDescription {

    final In<InCsv> inCsv;

    final Out<OutParq> outParq;

    public ParqDecimalJob(
            @Import(name = "inCsv", description = InFromCsv.class)
            In<InCsv> inCsv,
            @Export(name = "outParq", description = OutToParquet.class)
            Out<OutParq> outParq){
        this.inCsv = inCsv;
        this.outParq = outParq;
    }

    @Override
    protected void describe() {
        CoreOperatorFactory core = new CoreOperatorFactory();
        outParq.add(core.restructure(inCsv, OutParq.class));
    }
}
