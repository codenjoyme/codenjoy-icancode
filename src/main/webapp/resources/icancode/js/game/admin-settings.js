/*-
 * #%L
 * iCanCode - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
var AdminSettings = function(contextPath, settingsName) {
    var url = '/' + contextPath + '/settings/icancode/' + settingsName;

    var load = function(onSuccess) {
        $.get(url, null, onSuccess, 'json');
    }

    var save = function(info, onSuccess, onError) {
        $.ajax({
            type: "POST",
            url: url,
            data: JSON.stringify(info),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: onSuccess,
            failure: onError
        });
    }

    return {
        load : load,
        save : save
    }
}