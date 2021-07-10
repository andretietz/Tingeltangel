/*
 * Copyright 2016 martin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tingeltangel.cli_ng;

import java.io.IOException;

/**
 *
 * @author martin
 */
class SaveBook extends CliCmd {

    @Override
    public String getName() {
        return("save-book");
    }

    @Override
    public String getDescription() {
        return("save-book");
    }

    @Override
    public int execute(String[] args) {
        try {
            CLI.getBook().save();
        } catch (IOException ex) {
            return(error("Buch konnte nicht gespeichert werden", ex));
        }
        return(ok());
    }

    
}